package com.manzo.slang.extensions

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.support.annotation.RequiresPermission
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

/**
 * Created by Manolo D'Antonio on 29/08/2019
 */


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
    Handler().postDelayed(block, waitMillis)
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
    return (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
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
 * @param ipAddress String
 * @return String
 */
fun getMacFromARP(ipAddress: String) =
    File("/proc/net/arp").run {
        findLine(ipAddress).findMAC()
    }


/**
 * Returns a list of Pair<IpAddres, MacAddress>
 * @param ipList List<String>
 * @return List<Pair<String, String>>
 */
fun getMacFromARP(ipList: List<String>): List<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()
    ipList.forEach {
        result.add(Pair(it, getMacFromARP(it)))
    }
    return result
}


/**
 * Checks if this address is available on the wifi network.
 *
 * WARNING! DON'T RUN ON MAIN THREAD!
 *
 * Always false if on the main thread!
 *
 * @param address String
 * @param port Int
 * @param scanTimeoutMillis Int
 * @return Boolean
 */
fun checkAddressReachable(address: String, port: Int = 22, scanTimeoutMillis: Int = 500): Boolean {
    val sockaddr = InetSocketAddress(address, port)
    val socket = Socket()
    var online = true

    try {
        socket.connect(sockaddr, scanTimeoutMillis)
    } catch (e: Exception) {
        online = false
        e.logError()
    } finally {
        try {
            socket.close()
        } catch (e: IOException) {
            // close() operation can also throw an IOException
        }

        return online
    }
}

/**
 * Generates random hex String
 * @return String
 */
fun generateRandomHex(): String =
    Integer.toHexString(Random().nextInt())

/**
 * Returns current clock time.
 * @param printSeconds Boolean
 * @return String
 */
fun getTime(printSeconds: Boolean = false): String {
    GregorianCalendar().run {
        val hour = get(Calendar.HOUR_OF_DAY)
        val minute = get(Calendar.MINUTE)

        return "$hour:$minute".let {
            if (printSeconds) it.plus(":${get(Calendar.SECOND)}")
            else it
        }
    }
}
