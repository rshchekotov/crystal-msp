package org.doomlabs.crystal.msp

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.doomlabs.crystal.msp.event.filter.HopperFilterPipe
import org.doomlabs.crystal.msp.event.inventory.InvBucketCraft
import org.doomlabs.crystal.msp.event.join.ResourcePackLoader
import java.net.URL
import java.util.logging.Logger

@Suppress("unused")
class Crystal : JavaPlugin() {
    init {
        instance = this
    }

    override fun onEnable() {
        loadResourcePack()
        loadDataPack()

        Bukkit.getScheduler().runTaskLater(this, { ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "datapack list")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "datapack enable \"file/crystal.zip\"")
        }, 1L)

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
        val datapack = Bukkit.getWorldContainer().toPath()
            .resolve(Bukkit.getWorlds()[0].name)
            .resolve("datapacks")
            .resolve("crystal.zip").toFile()

        // TODO: Version Check
        if(datapack.exists()) return

        val latestHash = this::class.java.getResourceAsStream("/latest-data.sha1")?.bufferedReader()?.readLine()
        Bukkit.getLogger().info("Crystal Data Hash: $latestHash")

        val sha1 = URL(remoteRepository("src/generated/resources/crystal-data.sha1")).readText()
        val data = URL(remoteRepository("src/generated/resources/crystal-data.zip"))

        if(sha1 != latestHash) {
            Bukkit.getLogger().warning("Crystal Data Hash Mismatch: $sha1 != $latestHash")
        }

        Bukkit.getLogger().info("Started installing Crystal Data Pack")
        data.openStream().use { input ->
            datapack.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Bukkit.getLogger().info("Finished installing Crystal Data Pack")
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