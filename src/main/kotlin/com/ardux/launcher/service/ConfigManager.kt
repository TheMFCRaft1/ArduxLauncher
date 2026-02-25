package com.ardux.launcher.service

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

@Serializable
data class AppConfig(
    var isFirstRun: Boolean = true,
    var userName: String = "Ardux Player",
    var theme: String = "Default",
    var libraryPath: String = "/home/max/ArduxGames"
)

object ConfigManager {
    private val configFile = File(System.getProperty("user.home"), ".ardux_launcher.json")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    
    var config: AppConfig = load()

    fun load(): AppConfig {
        return if (configFile.exists()) {
            try {
                json.decodeFromString<AppConfig>(configFile.readText())
            } catch (e: Exception) {
                AppConfig()
            }
        } else {
            AppConfig()
        }
    }

    fun save() {
        configFile.writeText(json.encodeToString(config))
    }
}
