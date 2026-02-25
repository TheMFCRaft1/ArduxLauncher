package com.ardux.launcher.service

import com.ardux.launcher.model.Game
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class GameMetadata(
    val title: String,
    val developer: String = "Unknown",
    val version: String = "1.0.0",
    val description: String = "",
    val icon: String = "icon.png",
    val executable: String = "start.sh"
)

class GameScanner(val libraryPath: String) {
    private val json = Json { ignoreUnknownKeys = true }

    fun scan(): List<Game> {
        val root = File(libraryPath)
        if (!root.exists() || !root.isDirectory) return emptyList()

        return root.listFiles { file -> file.isDirectory }?.mapNotNull { dir ->
            val metaFile = File(dir, "metadata.json")
            if (metaFile.exists()) {
                try {
                    val metadata = json.decodeFromString<GameMetadata>(metaFile.readText())
                    Game(
                        id = dir.name,
                        title = metadata.title,
                        developer = "${metadata.developer} (v${metadata.version})",
                        iconPath = File(dir, metadata.icon).takeIf { it.exists() }?.absolutePath,
                        executablePath = File(dir, metadata.executable).absolutePath
                    )
                } catch (e: Exception) {
                    println("Failed to parse metadata for ${dir.name}: ${e.message}")
                    null
                }
            } else {
                null
            }
        } ?: emptyList()
    }
}
