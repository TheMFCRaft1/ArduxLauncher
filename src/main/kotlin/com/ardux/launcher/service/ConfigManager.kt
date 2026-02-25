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
    private val configDir = File(System.getProperty("user.home"), ".config/ardux")
    private val configFile = File(configDir, "config.json")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    
    var config: AppConfig = load()

    fun load(): AppConfig {
        if (!configDir.exists()) {
            configDir.mkdirs()
        }
        
        return if (configFile.exists()) {
            try {
                json.decodeFromString<AppConfig>(configFile.readText())
            } catch (e: Exception) {
                AppConfig()
            }
        } else {
            val config = AppConfig()
            // Ensure default library path exists
            File(config.libraryPath).mkdirs()
            config
        }
    }

    fun save() {
        if (!configDir.exists()) {
            configDir.mkdirs()
        }
        configFile.writeText(json.encodeToString(config))
    }

    fun reset() {
        configFile.delete()
        config = AppConfig()
    }
}
