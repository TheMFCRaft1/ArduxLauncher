package com.ardux.launcher.ui.screens

import com.ardux.launcher.model.Game
import com.ardux.launcher.ui.components.GameCard
import com.ardux.launcher.ui.components.PowerMenu
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeScreen : BorderPane() {
    init {
        // Top Bar (Status)
        val statusBar = HBox()
        statusBar.styleClass.add("status-bar")
        statusBar.alignment = Pos.CENTER_RIGHT
        statusBar.spacing = 20.0
        
        val timeLabel = Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
        timeLabel.styleClass.add("status-label")
        
        val wifiLabel = Label(getWlanStatus())
        wifiLabel.styleClass.add("status-label")
        
        val batteryLabel = Label("100%")
        batteryLabel.styleClass.add("status-label")

        val settingsBtn = Button("⚙").apply {
            styleClass.add("power-button")
            setOnAction {
                val root = scene.root as StackPane
                root.children.add(SettingsScreen {
                    root.children.removeLast()
                })
            }
        }

        val powerBtn = Button("⏻").apply {
            styleClass.add("power-button")
            setOnAction {
                val menu = PowerMenu(scene.window as javafx.stage.Stage)
                val bounds = localToScreen(boundsInLocal)
                menu.show(scene.window, bounds.maxX - 320, bounds.minY + 60)
            }
        }
        
        statusBar.children.addAll(wifiLabel, batteryLabel, timeLabel, settingsBtn, powerBtn)
        top = statusBar

        // Center Content (Game Grid)
        val grid = TilePane()
        grid.hgap = 30.0
        grid.vgap = 30.0
        grid.padding = Insets(40.0)
        grid.prefColumns = 4
        grid.alignment = Pos.TOP_LEFT

        val scanner = com.ardux.launcher.service.GameScanner("/home/max/ArduxGames")
        var games = scanner.scan()
        
        if (games.isEmpty()) {
            games = listOf(
                Game("1", "Cyber Ardux", "Ardux Studio", null, "/usr/bin/echo"),
                Game("2", "Neon Racer", "Velocity Games", null, "/usr/bin/echo"),
                Game("3", "Void Runner", "Cosmic Dev", null, "/usr/bin/echo"),
                Game("4", "Pulse Strike", "Impact Labs", null, "/usr/bin/echo"),
                Game("5", "Meta Quest", "Quantum Pixels", null, "/usr/bin/echo")
            )
        }

        games.forEach { game ->
            grid.children.add(GameCard(game))
        }

        val scrollPane = ScrollPane(grid)
        scrollPane.isFitToWidth = true
        scrollPane.style = "-fx-background: transparent; -fx-background-color: transparent;"
        
        center = VBox(
            Label("Library").apply { 
                styleClass.add("category-header")
                padding = Insets(20.0, 0.0, 0.0, 40.0)
            },
            scrollPane
        ).apply { spacing = 10.0 }
    }

    private fun getWlanStatus(): String {
        return try {
            val netPath = java.io.File("/sys/class/net")
            val interfaces = netPath.listFiles { _, name -> name.startsWith("w") } ?: emptyArray()
            
            var isConnected = false
            for (iface in interfaces) {
                val operstate = java.io.File(iface, "operstate").readText().trim()
                if (operstate == "up") {
                    isConnected = true
                    break
                }
            }
            
            if (isConnected) "WiFi: Connected" else "WiFi: Disconnected"
        } catch (e: Exception) {
            "WiFi: Unknown"
        }
    }
}
