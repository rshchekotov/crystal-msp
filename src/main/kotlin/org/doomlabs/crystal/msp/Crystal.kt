package org.doomlabs.crystal.msp

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.doomlabs.crystal.msp.event.filter.HopperFilterPipe
import org.doomlabs.crystal.msp.event.inventory.InvBucketCraft
import org.doomlabs.crystal.msp.event.join.ResourcePackLoader
import org.doomlabs.crystal.msp.util.unzip
import java.net.URL
import java.util.logging.Logger
import kotlin.io.path.createTempFile

@Suppress("unused")
class Crystal : JavaPlugin() {
    init {
        instance = this
    }

    override fun onEnable() {
        loadDataPack()
        loadResourcePack()

        for(player in Bukkit.getOnlinePlayers()) {
            player.setResourcePack(resourcePack, resourcePackHash, true)
        }

        register(ResourcePackLoader)
        register(InvBucketCraft)
        register(HopperFilterPipe)
    }

    private fun loadResourcePack() {
        val latestHash = this::class.java.getResourceAsStream("/latest-resources.sha1")?.bufferedReader()?.readLine()
        Bukkit.getLogger().info("Crystal Resources Hash: $latestHash")

        resourcePackHash = URL(remoteRepository("src/generated/resources/crystal-resources.sha1")).readText()
        resourcePack = remoteRepository("src/generated/resources/crystal-resources.zip")

        if(latestHash != resourcePackHash) {
            Bukkit.getLogger().warning("Crystal Resources Hash Mismatch: $latestHash != $resourcePackHash")
        }

    }

    private fun loadDataPack() {
        val latestHash = this::class.java.getResourceAsStream("/latest-data.sha1")?.bufferedReader()?.readLine()
        Bukkit.getLogger().info("Crystal Data Hash: $latestHash")

        val dataFile = createTempFile("datapack", ".zip").toFile()
        dataFile.deleteOnExit()
        val sha1 = URL(remoteRepository("src/generated/resources/crystal-data.sha1")).readText()
        val data = URL(remoteRepository("src/generated/resources/crystal-data.zip"))

        if(sha1 != latestHash) {
            Bukkit.getLogger().warning("Crystal Data Hash Mismatch: $sha1 != $latestHash")
        }

        data.openStream().use { input ->
            dataFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val datapackDirectory = Bukkit.getWorldContainer().toPath()
            .resolve("plugins")
            .resolve("datapacks")
            .resolve("crystal")
        unzip(dataFile, datapackDirectory.toFile())
        Bukkit.reloadData()
    }

    companion object {
        /* Repository Interaction */
        private const val rawGitHub = "https://raw.githubusercontent.com"
        private const val repository = "rshchekotov/crystal-msp"

        private fun remoteRepository(path: String): String {
            return "$rawGitHub/$repository/master/$path"
        }

        private fun register(listener: Listener) {
            Bukkit.getPluginManager().registerEvents(listener, instance)
        }

        lateinit var instance: Crystal
            private set

        val logger: Logger
            get() = instance.logger

        /* Resource Loading */
        lateinit var resourcePack: String
            private set

        lateinit var resourcePackHash: String
            private set
    }
}