package com.ardux.launcher.ui.screens

import com.ardux.launcher.service.ConfigManager
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color

class SetupScreen(private val onComplete: () -> Unit) : VBox() {
    private var currentStep = 1
    private val contentArea = VBox().apply { 
        alignment = Pos.CENTER
        spacing = 20.0
    }

    init {
        styleClass.add("main-container")
        alignment = Pos.CENTER
        spacing = 30.0
        padding = Insets(50.0)

        val title = Label("Welcome to Ardux").apply {
            styleClass.add("setup-title")
        }

        val subtitle = Label("Let's personalize your console experience.").apply {
            styleClass.add("status-label")
        }

        children.addAll(title, subtitle, Region().apply { minHeight = 20.0 }, contentArea)
        showStep(1)
    }

    private fun showStep(step: Int) {
        contentArea.children.clear()
        when (step) {
            1 -> showIdentityStep()
            2 -> showWlanStep()
        }
    }

    private fun showIdentityStep() {
        val instructionTitle = Label("Step 1: Your Identity").apply {
            styleClass.add("category-header")
            style = "-fx-font-size: 24;"
        }

        val nameField = TextField(ConfigManager.config.userName).apply {
            promptText = "e.g. ArduxPlayer123"
            maxWidth = 400.0
            styleClass.add("input-field")
        }

        val nextButton = Button("Next: Network").apply {
            styleClass.add("primary-button")
            setOnAction {
                if (nameField.text.isNotBlank()) {
                    ConfigManager.config.userName = nameField.text
                }
                showStep(2)
            }
        }

        contentArea.children.addAll(instructionTitle, nameField, nextButton)
    }

    private fun showWlanStep() {
        val instructionTitle = Label("Step 2: Connect to WiFi").apply {
            styleClass.add("category-header")
            style = "-fx-font-size: 24;"
        }

        val networkList = VBox().apply { 
            spacing = 10.0
            alignment = Pos.CENTER
            maxWidth = 500.0
        }

        fun refreshNetworks() {
            networkList.children.clear()
            networkList.children.add(Label("Scanning...").apply { style = "-fx-text-fill: -ardux-text-dim;" })
            
            // Run scan in background to avoid UI freeze
            Thread {
                val networks = com.ardux.launcher.service.NetworkService.scanWlan()
                javafx.application.Platform.runLater {
                    networkList.children.clear()
                    if (networks.isEmpty()) {
                        networkList.children.add(Label("No networks found.").apply { style = "-fx-text-fill: -ardux-text-dim;" })
                    } else {
                        networks.take(5).forEach { net ->
                            val btn = Button("${net.bars} ${net.ssid}").apply {
                                styleClass.add("power-menu-button")
                                setOnAction { showConnectDialog(net.ssid) }
                            }
                            networkList.children.add(btn)
                        }
                    }
                    networkList.children.add(Button("Skip for now").apply {
                        styleClass.add("power-menu-button")
                        style = "-fx-background-color: transparent; -fx-text-fill: -ardux-text-dim;"
                        setOnAction { finishSetup() }
                    })
                }
            }.start()
        }

        val refreshBtn = Button("Scan for Networks").apply {
            styleClass.add("primary-button")
            setOnAction { refreshNetworks() }
        }

        contentArea.children.addAll(instructionTitle, networkList, refreshBtn)
        refreshNetworks()
    }

    private fun showConnectDialog(ssid: String) {
        contentArea.children.clear()
        val title = Label("Connect to $ssid").apply { styleClass.add("category-header") }
        val passField = javafx.scene.control.PasswordField().apply {
            promptText = "Password"
            maxWidth = 400.0
            styleClass.add("input-field")
        }
        val connectBtn = Button("Connect").apply {
            styleClass.add("primary-button")
            setOnAction {
                val success = com.ardux.launcher.service.NetworkService.connectWlan(ssid, passField.text)
                if (success) finishSetup()
                else {
                    // Show error (optional, simplified for now)
                    showWlanStep()
                }
            }
        }
        val backBtn = Button("Back").apply {
            styleClass.add("power-menu-button")
            setOnAction { showWlanStep() }
        }
        contentArea.children.addAll(title, passField, connectBtn, backBtn)
    }

    private fun finishSetup() {
        ConfigManager.config.isFirstRun = false
        ConfigManager.save()
        onComplete()
    }
}
