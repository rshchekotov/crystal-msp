package org.doomlabs.crystal.msp

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.doomlabs.crystal.msp.event.filter.HopperFilterPipe
import org.doomlabs.crystal.msp.event.inventory.InvBucketCraft
import java.util.logging.Logger

@Suppress("unused")
class Crystal : JavaPlugin() {
    init {
        instance = this
    }

    override fun onEnable() {
        loadDataPack()
        loadResourcePack()

        Bukkit.getPluginManager().registerEvents(InvBucketCraft, this)
        Bukkit.getPluginManager().registerEvents(HopperFilterPipe, this)
    }

    private fun loadResourcePack() {
        val resourceHash = this::class.java.getResourceAsStream("/latest-resources.sha1")?.bufferedReader()?.readLine()
        Bukkit.getLogger().info("Crystal Resources Hash: $resourceHash")
    }

    private fun loadDataPack() {
        val dataHash = this::class.java.getResourceAsStream("/latest-data.sha1")?.bufferedReader()?.readLine()
        Bukkit.getLogger().info("Crystal Data Hash: $dataHash")
    }

    companion object {
        lateinit var instance: Crystal
            private set

        val logger: Logger
            get() = instance.logger
    }
}