package com.ardux.launcher.service

import com.ardux.launcher.model.Game
import java.io.File

class GameLauncher {
    fun launch(game: Game) {
        try {
            val processBuilder = ProcessBuilder("sh", game.executablePath)
            processBuilder.directory(File(game.executablePath).parentFile)
            processBuilder.start()
            println("Successfully launched ${game.title}")
        } catch (e: Exception) {
            println("Failed to launch ${game.title}: ${e.message}")
        }
    }
}
