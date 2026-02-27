/////////////////////////////
// Plugins
/////////////////////////////
// Kotlin JVM, Kotlin Serialization, JavaFX, ShadowJar, Application
plugins {
    kotlin("jvm") version "1.9.22"                          // Kotlin JVM Support
    kotlin("plugin.serialization") version "1.9.22"         // Kotlin Serialization Support
    id("org.openjfx.javafxplugin") version "0.1.0"          // JavaFX Plugin
    id("com.github.johnrengelman.shadow") version "8.1.1"   // ShadowJar Plugin (für Fat Jars)
    application                                             // Java Application Plugin
}

/////////////////////////////
// Projekt Meta
/////////////////////////////
group = "com.ardux.launcher"
version = "1.0-SNAPSHOT"

/////////////////////////////
// Repositories
/////////////////////////////
repositories {
    mavenCentral()
}

/////////////////////////////
// Dependencies
/////////////////////////////
dependencies {
    implementation(kotlin("stdlib"))                                         // Kotlin Standard Library
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2") // JSON Serialization
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")               // Icon Support für JavaFX
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.3.1") // Material Design Icons
}

/////////////////////////////
// JavaFX Konfiguration
/////////////////////////////
val javafxPlatform = project.findProperty("javafxPlatform")?.toString() ?: "linux"

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml", "javafx.graphics")
}

dependencies {
    implementation("org.openjfx:javafx-graphics:21:$javafxPlatform")
    implementation("org.openjfx:javafx-controls:21:$javafxPlatform")
    implementation("org.openjfx:javafx-base:21:$javafxPlatform")
}

/////////////////////////////
// Application Hauptklasse
/////////////////////////////
application {
    mainClass.set("com.ardux.launcher.MainKt")
}

/////////////////////////////
// Kotlin Compile Optionen
/////////////////////////////
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21" // Java 21 Target
}

/////////////////////////////
// ShadowJar Task (Fat Jar)
// Erstellt eine ausführbare JAR mit allen Dependencies
/////////////////////////////
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("ArduxLauncher-$javafxPlatform")   // Name der JAR inkl. Plattform
    archiveClassifier.set("all")           // Klassifizierer für Fat Jar
    archiveVersion.set("1.0-SNAPSHOT")     // Versionsnummer
    manifest {
        attributes["Main-Class"] = "com.ardux.launcher.MainKt" // Main-Klasse im Manifest
    }
    mergeServiceFiles()                     // Services aus allen Jars zusammenführen
}

/////////////////////////////
// InstallLauncher Task
// Kopiert die ShadowJar in ein Install-Verzeichnis (portable Distribution)
// Kein jlink / Module nötig, funktioniert direkt mit Java 21
/////////////////////////////
tasks.register<Copy>("installLauncher") {
    dependsOn("shadowJar")                          // Muss ShadowJar vorher gebaut sein
    from(tasks.named("shadowJar"))                 // Quelle: Fat Jar
    into("$buildDir/install/ArduxLauncher-$javafxPlatform/lib")    // Zielverzeichnis inkl. Plattform
    doLast {
        println("ArduxLauncher ready in build/install/ArduxLauncher-$javafxPlatform/")
    }
}