package com.ardux.launcher

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import com.ardux.launcher.ui.screens.HomeScreen
import com.ardux.launcher.ui.screens.SetupScreen
import javafx.scene.paint.Color

class Main : Application() {
    override fun start(primaryStage: Stage) {
        val root = StackPane()
        root.styleClass.add("main-container")
        
        fun showMain() {
            root.children.clear()
            root.children.add(HomeScreen())
        }

        if (com.ardux.launcher.service.ConfigManager.config.isFirstRun) {
            root.children.add(SetupScreen { showMain() })
        } else {
            showMain()
        }
        
        val scene = Scene(root, 1280.0, 720.0)
        scene.fill = Color.TRANSPARENT
        scene.stylesheets.add(javaClass.getResource("/com/ardux/launcher/styles/ArduxStyles.css")?.toExternalForm())
        
        primaryStage.title = "ArduxLauncher 1.0"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
