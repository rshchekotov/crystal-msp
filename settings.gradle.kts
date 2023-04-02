@file:Suppress("SpellCheckingInspection")

rootProject.name = "CrystalMSP"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        val kotlinVersion: String by settings

        kotlin("jvm") version kotlinVersion
        kotlin("kapt") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion

        id("com.github.johnrengelman.shadow") version "7.1.2"
        /*
         * TODO: As soon as this plugin fully supports Folia,
         *     switch to using it instead of the Paper.
         */
        id("xyz.jpenilla.run-paper") version "2.0.0"
        id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    }
}