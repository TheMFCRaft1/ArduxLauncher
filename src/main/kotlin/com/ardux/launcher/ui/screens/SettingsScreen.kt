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

            val cancelBtn = Button("Cancel").apply {
                styleClass.add("power-menu-button")
                setOnAction { onBack() }
            }
            
            children.addAll(saveBtn, cancelBtn)
        }

        children.addAll(title, settingsBox, actionBox)
    }
}
