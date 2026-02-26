plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

group = "com.ardux.launcher"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.3.1")
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml", "javafx.graphics")
}

application {
    mainClass.set("com.ardux.launcher.MainKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

// -----------------------------
// Shadow / fat jar
// -----------------------------
plugins.apply("com.github.johnrengelman.shadow")

tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("ArduxLauncher")
    archiveClassifier.set("all")
    archiveVersion.set("1.0-SNAPSHOT")
    manifest {
        attributes["Main-Class"] = "com.ardux.launcher.MainKt"
    }
    mergeServiceFiles()
}

// -----------------------------
// InstallDist (ohne Module / jlink)
// -----------------------------
tasks.register<Copy>("installLauncher") {
    dependsOn("build")
    from(tasks.named("jar"))
    into("$buildDir/install/ArduxLauncher/lib")
    doLast {
        println("ArduxLauncher ready in build/install/ArduxLauncher/")
    }
}