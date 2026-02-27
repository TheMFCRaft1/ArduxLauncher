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
    private val timeLabel = Label()
    private val wifiLabel = Label()
    private val btLabel = Label()
    private val batteryLabel = Label("100%")

    init {
        // Top Bar (Status) - Use StackPane to center the clock
        val statusBar = StackPane()
        statusBar.styleClass.add("status-bar")

        // Center: Clock
        timeLabel.styleClass.add("status-label")
        timeLabel.style = "-fx-font-size: 18; -fx-text-fill: white;"
        
        val centerBox = HBox(timeLabel)
        centerBox.alignment = Pos.CENTER
        centerBox.isMouseTransparent = true // Don't block clicks to things behind it if necessary

        // Right side: Icons and Buttons
        val rightBar = HBox()
        rightBar.alignment = Pos.CENTER_RIGHT
        rightBar.spacing = 15.0
        
        wifiLabel.styleClass.add("status-label")
        btLabel.styleClass.add("status-label")
        batteryLabel.styleClass.add("status-label")

        val settingsBtn = Button("⚙").apply {
            styleClass.add("power-button")
            setOnAction {
                val root = scene.root as StackPane
                root.children.add(SettingsScreen {
                    root.children.removeLast()
                    updateStatus() // Refresh status when coming back from settings
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
        
        rightBar.children.addAll(wifiLabel, btLabel, batteryLabel, settingsBtn, powerBtn)
        
        statusBar.children.addAll(centerBox, rightBar)
        top = statusBar

        // Start periodic updates
        startStatusUpdates()

        // Center Content (Game Grid)
        val grid = TilePane()
        grid.hgap = 30.0
        grid.vgap = 30.0
        grid.padding = Insets(40.0)
        grid.prefColumns = 4
        grid.alignment = Pos.TOP_LEFT

        val scanner = com.ardux.launcher.service.GameScanner(com.ardux.launcher.service.ConfigManager.config.libraryPath)
        val games = scanner.scan()
        
        if (games.isEmpty()) {
            grid.children.add(Label("No games found in ${scanner.libraryPath}. Add metadata.json to your game folders.").apply {
                style = "-fx-text-fill: -ardux-text-dim; -fx-padding: 20;"
            })
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

    private fun startStatusUpdates() {
        val timeline = javafx.animation.Timeline(
            javafx.animation.KeyFrame(javafx.util.Duration.seconds(1.0), {
                updateStatus()
            })
        )
        timeline.cycleCount = javafx.animation.Animation.INDEFINITE
        timeline.play()
        updateStatus() // Initial update
    }

    private fun updateStatus() {
        val now = LocalDateTime.now()
        timeLabel.text = now.format(DateTimeFormatter.ofPattern("HH:mm"))
        
        val wlanStatus = com.ardux.launcher.service.NetworkService.getWlanStatus()
        wifiLabel.text = if (wlanStatus == "Disconnected") "󰤮 " else "󰤨 $wlanStatus"
        
        val btEnabled = com.ardux.launcher.service.NetworkService.isBluetoothEnabled()
        btLabel.text = if (btEnabled) "󰂯" else "󰂲"
        
        // Battery status could be added here if needed via system commands
    }
}
