package com.ardux.launcher.ui.components

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.stage.Popup
import javafx.stage.Stage
import kotlin.system.exitProcess

class PowerMenu(owner: Stage) : Popup() {
    init {
        val root = VBox().apply {
            styleClass.add("power-menu-container")
            alignment = Pos.CENTER
            minWidth = 300.0
        }

        val title = Label("Power Options").apply {
            styleClass.add("power-menu-title")
        }

        val offBtn = createPowerButton("Power Off") {
            try {
                ProcessBuilder("systemctl", "poweroff").start()
            } catch (e: Exception) {
                println("Failed to power off: ${e.message}")
            }
            exitProcess(0)
        }

        val restartBtn = createPowerButton("Restart") {
            try {
                ProcessBuilder("systemctl", "reboot").start()
            } catch (e: Exception) {
                println("Failed to restart: ${e.message}")
            }
            exitProcess(0)
        }

        val exitBtn = createPowerButton("Exit to Desktop") {
            exitProcess(0)
        }

        val closeBtn = createPowerButton("Cancel") {
            hide()
        }

        root.children.addAll(title, offBtn, restartBtn, exitBtn, closeBtn)
        content.add(root)
        
        isAutoHide = true
        
        // Load stylesheet for the popup
        scene.stylesheets.add(javaClass.getResource("/com/ardux/launcher/styles/ArduxStyles.css")?.toExternalForm())
    }

    private fun createPowerButton(text: String, action: () -> Unit): Button {
        return Button(text).apply {
            styleClass.add("power-menu-button")
            setOnAction { action() }
        }
    }
}
