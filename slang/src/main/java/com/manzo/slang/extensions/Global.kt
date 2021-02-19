package com.manzo.slang.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresPermission
import com.manzo.slang.helpers.ArpScanner
import com.manzo.slang.helpers.DEFAULT_SCAN_PORT
import com.manzo.slang.helpers.DEFAULT_SCAN_TIMEOUT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Manolo D'Antonio on 29/08/2019
 */

private fun getWifiManager(context: Context) =
    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager


/**
 * Get app debug logs
 */
fun getLogs() =
    Runtime.getRuntime().exec("logcat -d -v long *:D").run {
        inputStream.bufferedReader().use {
            it.readText()
        }
    }


/**
 * Delays operations in the block function
 * @param waitMillis Long
 * @param block Function0<Unit>
 */
fun delayed(waitMillis: Long = 1000, block: () -> Unit) {
    Handler().postDelayed({ block() }, waitMillis)
}

/**
 * Checks network. Requires ACCESS_NETWORK_STATE permission.
 * @param context Context
 * @return Boolean
 */
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

/**
 * Negation of [isNetworkAvailable]
 * @param context Context
 * @return Boolean
 */
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isNetworkUnavailable(context: Context) = !isNetworkAvailable(context)

/**
 * Retrieves device IP address from wifi. Requires ACCESS_WIFI_STATE permission.
 * @param context Context
 * @return String
 */
@RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
fun getMyIPfromWifi(context: Context): String {
    return (getWifiManager(context))
        .run {
            connectionInfo.ipAddress
        }
        .run {
            ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(this)
                .array()
        }
        .run {
            InetAddress.getByAddress(this).hostAddress
        }
}


/**
 * Get host name of the provided IP.
 *
 * UNRELIABLE -> https://stackoverflow.com/a/7800008/4473512
 * @param hostIp String
 * @return String
 */
fun getHostName(hostIp: String): String = InetAddress.getByName(hostIp).canonicalHostName

/**
 * Searches ARP table of the device for a MAC address matching the provided IP
 * This will not work on Android 10+
 * @param ipAddress String
 * @return String
 */
@TargetApi(28)
private fun getMacFromARP(ipAddress: String) =
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        try {
            File("/proc/net/arp").run {
                findLine(ipAddress).findMAC()
            }
        } catch (e: java.lang.Exception) {
            ""
        }
    } else ""


/**
 * Returns a list of Pair<IpAddress, MacAddress>
 * This will not work on Android 10+, use [getMacFromAddress] or ArpScanner helper
 * @param ipList List<String>
 * @return List<Pair<String, String>>
 */
@Deprecated("Use getMacFromAddress or ArpScanner")
@TargetApi(28)
fun getMacFromARP(vararg ipList: String): List<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()
    ipList.forEach {
        result.add(Pair(it, getMacFromARP(it)))
    }
    return result
}

/**
 * Returns a list of Pair<IpAddress, MacAddress>
 * @param ipList List<String>
 * @return List<Pair<String, String>>
 */
@SuppressLint("HardwareIds")
suspend fun getMacFromAddress(context: Context, vararg ipList: String): List<Pair<String, String>> {
    return ArpScanner.getArpTable()
        .map { it.value }
        .filter { ipList.contains(it.ip.hostAddress) }
        .map { it.ip.hostAddress to it.hwAddress.address }
}


/**
 * Checks if this address is available on the wifi network.
 *
 * @param address String
 * @param port Int
 * @param scanTimeoutMillis Int
 * @return Boolean
 */
suspend fun checkAddressReachable(
    address: String,
    port: Int = DEFAULT_SCAN_PORT,
    scanTimeoutMillis: Int = DEFAULT_SCAN_TIMEOUT
): Boolean {

    return withContext(Dispatchers.IO) {

        val sockaddr = InetSocketAddress(address, port)
        val socket = Socket()
        var online = true

        runCatching { socket.connect(sockaddr, scanTimeoutMillis) }
            .onFailure {
                online = false
                it.logError()
            }

        // close() operation can also throw an IOException
        runCatching { socket.close() }
            .onFailure { it.logError() }

        online
    }

}

/**
 * Generates random hex String
 * @return String
 */
fun generateRandomHex(): String =
    Integer.toHexString(Random().nextInt())

/**
 * Returns current clock time. Default time format HH:mm:ss
 *
 * For time formats check [SimpleDateFormat]
 *
 * @param printSeconds Boolean
 * @param timeFormat String?
 * @return String
 */
fun getTime(printSeconds: Boolean = false, timeFormat: String? = null): String {
    val timePattern = timeFormat
        ?: "HH:mm".let {
            if (printSeconds) "$it:ss"
            else it
        }
    return SimpleDateFormat(timePattern, Locale.getDefault())
        .format(Calendar.getInstance().time)
}


/**
 * Returns current date. Default time format yyyy-MM-dd
 *
 * For time formats check [SimpleDateFormat]
 *
 *
 * @param showYear Boolean
 * @param timeFormat String?
 * @return String
 */
fun getDate(showYear: Boolean = true, timeFormat: String? = null): String {
    val timePattern = timeFormat
        ?: "MM-dd".let {
            if (showYear) "yyyy-$it"
            else it
        }
    return SimpleDateFormat(timePattern, Locale.getDefault())
        .format(Calendar.getInstance().time)
}


