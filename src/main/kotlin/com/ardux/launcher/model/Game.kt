package com.ardux.launcher.model

data class Game(
    val id: String,
    val title: String,
    val developer: String,
    val iconPath: String?,
    val executablePath: String,
    val category: String = "All Games"
)
