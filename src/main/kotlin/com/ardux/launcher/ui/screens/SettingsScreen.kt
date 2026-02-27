package com.ardux.launcher.ui.screens

import com.ardux.launcher.service.ConfigManager
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*

class SettingsScreen(private val onBack: () -> Unit) : VBox() {
    init {
        styleClass.add("main-container")
        alignment = Pos.CENTER
        spacing = 30.0
        padding = Insets(50.0)

        val title = Label("Settings").apply {
            styleClass.add("setup-title")
        }

        val settingsBox = VBox().apply {
            spacing = 20.0
            maxWidth = 600.0
            alignment = Pos.CENTER
            
            // User Name Setting
            val nameRow = HBox().apply {
                styleClass.add("settings-item")
                children.addAll(
                    Label("Player Name").apply { minWidth = 150.0 },
                    TextField(ConfigManager.config.userName).apply {
                        styleClass.add("input-field")
                        HBox.setHgrow(this, Priority.ALWAYS)
                        textProperty().addListener { _, _, newValue ->
                            ConfigManager.config.userName = newValue
                        }
                    }
                )
            }

            // Library Path Setting
            val pathRow = HBox().apply {
                styleClass.add("settings-item")
                children.addAll(
                    Label("Library Path").apply { minWidth = 150.0 },
                    TextField(ConfigManager.config.libraryPath).apply {
                        styleClass.add("input-field")
                        HBox.setHgrow(this, Priority.ALWAYS)
                        textProperty().addListener { _, _, newValue ->
                            ConfigManager.config.libraryPath = newValue
                        }
                    }
                )
            }

            children.addAll(nameRow, pathRow)
            
            // Connectivity Section
            children.add(Label("Connectivity").apply { 
                styleClass.add("category-header")
                style = "-fx-font-size: 20; -fx-padding: 20 0 10 0;"
            })

            val wifiRow = HBox().apply {
                styleClass.add("settings-item")
                val statusLabel = Label("WiFi: ${com.ardux.launcher.service.NetworkService.getWlanStatus()}")
                val scanBtn = Button("Scan & Connect").apply {
                    styleClass.add("primary-button")
                    style = "-fx-font-size: 14; -fx-padding: 8 20;"
                    setOnAction {
                        // For simplicity, we trigger the same logic as in SetupScreen
                        // In a real app, we might want a dedicated Overlay or Dialog
                        val root = scene.root as StackPane
                        val setupOverlay = VBox().apply {
                            styleClass.add("power-menu-container")
                            alignment = Pos.CENTER
                            spacing = 20.0
                            maxWidth = 600.0
                            maxHeight = 500.0
                            
                            val networkList = VBox().apply { spacing = 10.0; alignment = Pos.CENTER }
                            
                            fun refresh() {
                                networkList.children.clear()
                                networkList.children.add(Label("Scanning..."))
                                Thread {
                                    val nets = com.ardux.launcher.service.NetworkService.scanWlan()
                                    javafx.application.Platform.runLater {
                                        networkList.children.clear()
                                        nets.take(8).forEach { net ->
                                            networkList.children.add(Button("${net.bars} ${net.ssid}").apply {
                                                styleClass.add("power-menu-button")
                                                setOnAction { 
                                                    // Add password dialog logic here if needed, 
                                                    // keeping it simple for now as it's a POC
                                                    com.ardux.launcher.service.NetworkService.connectWlan(net.ssid, "") 
                                                    statusLabel.text = "WiFi: ${net.ssid}"
                                                    root.children.removeLast()
                                                }
                                            })
                                        }
                                        networkList.children.add(Button("Close").apply {
                                            styleClass.add("power-menu-button")
                                            setOnAction { root.children.removeLast() }
                                        })
                                    }
                                }.start()
                            }
                            
                            children.addAll(Label("Select Network").apply { styleClass.add("category-header") }, networkList)
                            refresh()
                        }
                        root.children.add(StackPane(setupOverlay).apply {
                            style = "-fx-background-color: rgba(0,0,0,0.5);"
                        })
                    }
                }
                children.addAll(statusLabel, Region().apply { HBox.setHgrow(this, Priority.ALWAYS) }, scanBtn)
            }

            val btRow = HBox().apply {
                styleClass.add("settings-item")
                val btEnabled = com.ardux.launcher.service.NetworkService.isBluetoothEnabled()
                val statusLabel = Label("Bluetooth: ${if (btEnabled) "On" else "Off"}")
                val toggleBtn = Button(if (btEnabled) "Disable" else "Enable").apply {
                    styleClass.add("primary-button")
                    style = "-fx-font-size: 14; -fx-padding: 8 20;"
                    setOnAction {
                        val current = com.ardux.launcher.service.NetworkService.isBluetoothEnabled()
                        com.ardux.launcher.service.NetworkService.toggleBluetooth(!current)
                        val nowEnabled = !current
                        statusLabel.text = "Bluetooth: ${if (nowEnabled) "On" else "Off"}"
                        text = if (nowEnabled) "Disable" else "Enable"
                    }
                }
                children.addAll(statusLabel, Region().apply { HBox.setHgrow(this, Priority.ALWAYS) }, toggleBtn)
            }

            children.addAll(wifiRow, btRow)
        }

        val actionBox = HBox().apply {
            spacing = 20.0
            alignment = Pos.CENTER
            
            val saveBtn = Button("Save & Back").apply {
                styleClass.add("primary-button")
                setOnAction {
                    ConfigManager.save()
                    onBack()
                }
            }

            val resetBtn = Button("Factory Reset").apply {
                style = "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 10;"
                setOnAction {
                    ConfigManager.reset()
                    // Delete library games (optional but requested)
                    java.io.File(ConfigManager.config.libraryPath).listFiles()?.forEach { it.deleteRecursively() }
                    kotlin.system.exitProcess(0)
                }
            }

            val cancelBtn = Button("Cancel").apply {
                styleClass.add("power-menu-button")
                setOnAction { onBack() }
            }
            
            children.addAll(saveBtn, resetBtn, cancelBtn)
        }

        children.addAll(title, settingsBox, actionBox)
    }
}
