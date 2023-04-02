@file:Suppress("SpellCheckingInspection", "VulnerableLibrariesLocal")

import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.jetbrains.kotlin.daemon.common.toHexString
import org.jetbrains.kotlin.incremental.deleteDirectoryContents
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")

    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
    id("net.minecrell.plugin-yml.bukkit")
}

val kotlinVersion: String by project
val kotlinSerializationVersion: String by project

group = "org.doomlabs.crystal.msp"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(kotlin("reflect", kotlinVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinSerializationVersion")
    implementation("net.kyori:adventure-extra-kotlin:4.7.0")

    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

    testImplementation(kotlin("test"))
}

tasks {
    val compressResources = register<Zip>("compressResources") {
        val base = "src/generated/resources"

        archiveFileName.set("crystal-resources.zip")
        destinationDirectory.set(file(base))
        from(file("src/main/resources/resourcepack"))

        doLast {
            val sha1 = MessageDigest.getInstance("SHA1")
                .digest(archiveFile.get().asFile.readBytes()).toHexString()
            file("$base/crystal-resources.sha1").writeText(sha1)
        }

        dependsOn("generateBukkitPluginDescription")
    }

    val compressData = register<Zip>("compressData") {
        val base = "src/generated/resources"

        archiveFileName.set("crystal-data.zip")
        destinationDirectory.set(file(base))
        from(file("src/main/resources/datapack"))

        doLast {
            val sha1 = MessageDigest.getInstance("SHA1")
                .digest(archiveFile.get().asFile.readBytes()).toHexString()
            file("$base/crystal-data.sha1").writeText(sha1)
        }

        dependsOn("generateBukkitPluginDescription")
    }

    val transferHash = register<Copy>("transferHash") {
        val base = "src/generated/resources"

        from(file("$base/crystal-resources.sha1"))
        from(file("$base/crystal-data.sha1"))
        into(file("build/resources/main"))

        dependsOn(processResources.get())
    }


    clean.get().doLast {
        file("src/generated/resources/").deleteDirectoryContents()
    }

    test.get().useJUnitPlatform()
    runServer.get().minecraftVersion("1.19.2")

    processResources.get().dependsOn(compressResources, compressData)
    jar.get().dependsOn(transferHash)
    build.get().dependsOn(project.tasks.named("shadowJar"))
}

kotlin {
    jvmToolchain(17)
}

bukkit {
    main = "org.doomlabs.crystal.msp.Crystal"
    apiVersion = "1.19"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("Doomer")
    prefix = "crystal"
    description = "Plugin for my Crystal Project"
}