package com.ardux.launcher.service

import com.ardux.launcher.model.Game
import java.io.File

class GameLauncher {
    fun launch(game: Game) {
        val executableFile = File(game.executablePath)
        
        if (!executableFile.exists()) {
            println("Executable not found: ${game.executablePath}")
            return
        }

        try {
            // Ensure the file is executable
            executableFile.setExecutable(true)

            val processBuilder = if (game.executablePath.endsWith(".sh")) {
                ProcessBuilder("sh", game.executablePath)
            } else {
                ProcessBuilder(game.executablePath)
            }

            processBuilder.directory(executableFile.parentFile)
            
            // Redirect output to avoid blocking
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD)
            processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD)
            
            processBuilder.start()
            println("Successfully launched ${game.title}")
        } catch (e: Exception) {
            println("Failed to launch ${game.title}: ${e.message}")
        }
    }
}
