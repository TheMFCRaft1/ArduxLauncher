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

        val instructionTitle = Label("Step 1: Your Identity").apply {
            styleClass.add("category-header")
            style = "-fx-font-size: 24;"
        }

        val instructionText = Label("Pick a handle that will be displayed across the system.").apply {
            style = "-fx-text-fill: -ardux-text-dim; -fx-font-size: 14;"
        }

        val nameField = TextField().apply {
            promptText = "e.g. ArduxPlayer123"
            maxWidth = 400.0
            styleClass.add("input-field")
        }

        val startButton = Button("Launch Ardux").apply {
            styleClass.add("primary-button")
            setOnAction {
                if (nameField.text.isNotBlank()) {
                    ConfigManager.config.userName = nameField.text
                }
                ConfigManager.config.isFirstRun = false
                ConfigManager.save()
                onComplete()
            }
        }

        children.addAll(title, subtitle, Region().apply { minHeight = 20.0 }, instructionTitle, instructionText, nameField, startButton)
    }
}
