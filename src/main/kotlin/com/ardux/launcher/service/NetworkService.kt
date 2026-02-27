package com.ardux.launcher.service

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

object NetworkService {

    fun scanWlan(): List<WlanNetwork> {
        return try {
            val process = ProcessBuilder("nmcli", "-t", "-f", "SSID,SIGNAL,BARS,SECURITY", "dev", "wifi", "list")
                .start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val networks = mutableListOf<WlanNetwork>()
            
            reader.forEachLine { line ->
                val parts = line.split(":")
                if (parts.size >= 4 && parts[0].isNotBlank()) {
                    networks.add(WlanNetwork(parts[0], parts[1].toIntOrNull() ?: 0, parts[2], parts[3]))
                }
            }
            process.waitFor(5, TimeUnit.SECONDS)
            networks.distinctBy { it.ssid }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun connectWlan(ssid: String, password: String): Boolean {
        return try {
            val process = if (password.isEmpty()) {
                ProcessBuilder("nmcli", "dev", "wifi", "connect", ssid).start()
            } else {
                ProcessBuilder("nmcli", "dev", "wifi", "connect", ssid, "password", password).start()
            }
            process.waitFor(10, TimeUnit.SECONDS)
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    fun getWlanStatus(): String {
        return try {
            val process = ProcessBuilder("nmcli", "-t", "-f", "ACTIVE,SSID", "dev", "wifi", "list").start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var connectedSsid = ""
            reader.forEachLine { line ->
                if (line.startsWith("yes:")) {
                    connectedSsid = line.substringAfter("yes:")
                }
            }
            process.waitFor(2, TimeUnit.SECONDS)
            if (connectedSsid.isNotEmpty()) connectedSsid else "Disconnected"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return try {
            val process = ProcessBuilder("rfkill", "list", "bluetooth").start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var enabled = false
            reader.forEachLine { line ->
                if (line.contains("Soft blocked: no")) {
                    enabled = true
                }
            }
            process.waitFor(2, TimeUnit.SECONDS)
            enabled
        } catch (e: Exception) {
            false
        }
    }

    fun toggleBluetooth(enable: Boolean): Boolean {
        return try {
            val command = if (enable) "unblock" else "block"
            val process = ProcessBuilder("rfkill", command, "bluetooth").start()
            process.waitFor(2, TimeUnit.SECONDS)
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }
}

data class WlanNetwork(
    val ssid: String,
    val signal: Int,
    val bars: String,
    val security: String
)
