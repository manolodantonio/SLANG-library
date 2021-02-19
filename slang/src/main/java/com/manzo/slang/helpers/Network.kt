package com.manzo.slang.helpers

import android.util.Log
import com.manzo.slang.extensions.checkAddressReachable
import com.manzo.slang.extensions.readAsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress

const val DEFAULT_SCAN_TIMEOUT = 500
const val DEFAULT_SCAN_PORT = 22

object ArpScanner {
    data class ArpEntry(val ip: InetAddress, val hwAddress: MacAddress) {
        companion object {
            fun from(ip: String, mac: String) = ArpEntry(
                InetAddress.getByName(ip),
                MacAddress(mac)
            )
        }
    }

    data class MacAddress(val address: String) {
        fun isBroadcast() = address == "00:00:00:00:00:00"
        fun getHiddenAddress() = address
            .substring(0, "aa:bb:cc".length) + ":XX:XX:XX"
    }

    private val TAG = ArpScanner.javaClass.name


    suspend fun getArpTable(): Map<InetAddress, ArpEntry> {

        return withContext(Dispatchers.Default) {
            listOf(
                async { getArpTableFromFile() },
                async { getArpTableFromIpCommand() })
                .awaitAll()
                .asSequence()
                .flatten()
                .filter { !it.hwAddress.isBroadcast() }
                .associateBy { it.ip }

        }
    }

    private suspend fun getArpTableFromFile() = withContext(Dispatchers.IO) {
        try {
            File("/proc/net/arp").inputStream().readAsTable()
                .drop(1)
                .filter { it.size == 6 }
                .map {
                    ArpEntry.from(it[0], it[3])
                }
        } catch (exception: FileNotFoundException) {
            Log.e(TAG, "arp file not found $exception")
            listOf<ArpEntry>()
        }
    }

    private suspend fun getArpTableFromIpCommand() =
        withContext(Dispatchers.IO) {
            try {

                val execution = Runtime.getRuntime().exec("ip neigh")
                execution.waitFor()
                execution.inputStream.readAsTable()
                    .filter { it.size >= 5 }
                    .map {
                        ArpEntry.from(it[0], it[4])
                    }
                    .onEach { Log.d(TAG, "found entry in 'ip neight': $it") }
            } catch (exception: IOException) {
                Log.e(TAG, "io error when running ip neigh $exception")
                listOf<ArpEntry>()
            }
        }
}


class PingScanner(
    private val myIP: String,
    private val scanTimeout: Int = DEFAULT_SCAN_TIMEOUT,
    private val scanPort: Int = -1,
    val onUpdate: (ScanResult) -> Unit
) {

    suspend fun pingIpAddresses(): List<ScanResult> =
        withContext(Dispatchers.IO) {
            val prefix = myIP.substring(0, myIP.lastIndexOf(".") + 1)

            generateSequence(1) {
                val next = it + 1
                if (next < 255) next else null
            }
                .map { Inet4Address.getByName(prefix + it) as Inet4Address }
                .chunked(10)
                .map { ipAddresses ->
                    async {
                        ipAddresses.map { ipAddress ->
                            val isReachable =
                                if (scanPort == -1) {
                                    ipAddress.isReachable(scanTimeout)
                                } else {
                                    checkAddressReachable(
                                        ipAddress.hostAddress,
                                        scanPort,
                                        scanTimeout
                                    )
                                }
                            val result =
                                ScanResult(
                                    ipAddress,
                                    isReachable,
                                    0.0
//                                                1.0 / network.networkSize
                                )
                            onUpdate(result)
                            result
                        }
                    }
                }
                .toList()
                .awaitAll()
                .flatten()
                .filter { it.isReachable }

        }

    data class ScanResult(
        val ipAddress: Inet4Address,
        val isReachable: Boolean,
        val progressIncrease: Double
    )
}