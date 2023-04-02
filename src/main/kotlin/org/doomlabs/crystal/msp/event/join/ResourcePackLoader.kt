package org.doomlabs.crystal.msp.event.join

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.doomlabs.crystal.msp.Crystal

object ResourcePackLoader: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.setResourcePack(Crystal.resourcePack, Crystal.resourcePackHash, true)
    }
}