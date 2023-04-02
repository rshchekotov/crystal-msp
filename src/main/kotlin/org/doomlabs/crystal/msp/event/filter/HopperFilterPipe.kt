package org.doomlabs.crystal.msp.event.filter

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryType

object HopperFilterPipe: Listener {
    @EventHandler
    fun onInventoryMoveItem(event: InventoryMoveItemEvent) {
        if(event.source.type != InventoryType.HOPPER) return
        // TODO: Routing Magic
    }
}