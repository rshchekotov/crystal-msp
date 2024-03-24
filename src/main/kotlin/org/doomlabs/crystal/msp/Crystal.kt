package org.doomlabs.crystal.msp

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.doomlabs.crystal.msp.event.filter.HopperFilterPipe
import org.doomlabs.crystal.msp.event.inventory.InvBucketCraft
import org.doomlabs.crystal.msp.event.join.ResourcePackLoader
import java.lang.management.ManagementFactory
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

        val pid = ManagementFactory.getRuntimeMXBean().pid
        this.getLogger().info("Crystal is loading on Bukkit ${Bukkit.getVersion()} @[P${pid}]")
        if (Bukkit.getMinecraftVersion() == "1.19.2") {
            this.getLogger().info("Crystal is loading for the correct Minecraft version.")
        } else {
            this.getLogger().info("This Version of Minecraft is not supported by Crystal - things may break.")
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
        val dataPack = Bukkit.getWorldContainer().toPath()
            .resolve(Bukkit.getWorlds()[0].name)
            .resolve("datapacks")
            .resolve("crystal.zip").toFile()

        val latestHash = this::class.java.getResourceAsStream("/latest-data.sha1")?.bufferedReader()?.readLine()
        Bukkit.getLogger().info("Crystal Data Hash: $latestHash")

        val sha1 = URL(remoteRepository("src/generated/resources/crystal-data.sha1")).readText()
        if(sha1 != latestHash) {
            Bukkit.getLogger().warning("Crystal Data Hash Mismatch: $sha1 != $latestHash")
        } else if(dataPack.exists()) return

        Bukkit.getLogger().info("Started installing Crystal Data Pack")
        val data = URL(remoteRepository("src/generated/resources/crystal-data.zip"))
        data.openStream().use { input ->
            dataPack.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Bukkit.getLogger().info("Finished installing Crystal Data Pack")
    }

    companion object {
        /* Repository Interaction */
        private const val RAW_GITHUB = "https://raw.githubusercontent.com"
        private const val REPO = "rshchekotov/crystal-msp"

        private fun remoteRepository(path: String): String {
            return "$RAW_GITHUB/$REPO/master/$path"
        }

        private fun register(listener: Listener) {
            Bukkit.getPluginManager().registerEvents(listener, instance)
        }

        lateinit var instance: Crystal
            private set

        val logger: Logger
            get() = instance.getLogger()

        /* Resource Loading */
        lateinit var resourcePack: String
            private set

        lateinit var resourcePackHash: String
            private set
    }
}