package com.ardux.launcher.ui.components

import com.ardux.launcher.model.Game
import javafx.animation.ScaleTransition
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.util.Duration

class GameCard(val game: Game) : VBox() {
    init {
        styleClass.add("game-card")
        alignment = Pos.CENTER
        spacing = 10.0
        isFocusTraversable = true

        val iconPlaceholder = Rectangle(180.0, 180.0)
        iconPlaceholder.arcWidth = 10.0
        iconPlaceholder.arcHeight = 10.0
        iconPlaceholder.style = "-fx-fill: linear-gradient(to bottom right, -ardux-primary, -ardux-secondary);"

        val titleLabel = Label(game.title)
        titleLabel.styleClass.add("game-title")

        val devLabel = Label(game.developer)
        devLabel.styleClass.add("status-label")

        children.addAll(iconPlaceholder, titleLabel, devLabel)

        // Animation logic
        val scaleIn = ScaleTransition(Duration.millis(200.0), this)
        scaleIn.toX = 1.05
        scaleIn.toY = 1.05

        val scaleOut = ScaleTransition(Duration.millis(200.0), this)
        scaleOut.toX = 1.0
        scaleOut.toY = 1.0

        focusedProperty().addListener { _, _, isFocused ->
            if (isFocused) {
                scaleIn.playFromStart()
            } else {
                scaleOut.playFromStart()
            }
        }
        
        setOnMouseClicked {
            launchGame()
        }
        
        setOnKeyPressed { event ->
            if (event.code.toString() == "ENTER") {
                launchGame()
            }
        }
    }

    private fun launchGame() {
        com.ardux.launcher.service.GameLauncher().launch(game)
    }
}
