package org.doomlabs.crystal.msp.event.inventory

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType.*
import org.bukkit.event.inventory.InventoryType.SlotType.CONTAINER
import org.bukkit.event.inventory.InventoryType.SlotType.QUICKBAR
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.doomlabs.crystal.msp.util.runBukkit

/**
 * Code to allow an infinite water source within your
 * inventory.
 * It is done by placing an empty bucket between two
 * full buckets of water, then - as soon as it is placed
 * or used, it is replenished.
 */
object InvBucketCraft : Listener {
    private fun checkInfiniteWater(inv: Inventory, slotType: SlotType, slot: Int, emptyBucket: ItemStack): Boolean {
        val legalInventories = listOf(BARREL, CHEST, PLAYER, ENDER_CHEST, SHULKER_BOX)
        val legalSlots = listOf(CONTAINER, QUICKBAR)
        if (inv.type !in legalInventories || slotType !in legalSlots) return false

        val isWaterBucket = { item: ItemStack? -> item?.type == Material.WATER_BUCKET }
        val width = 9

        if (slotType == QUICKBAR) {
            if(slot > 0 && slot < (width - 1)) {
                if (isWaterBucket(inv.getItem(slot - 1)) && isWaterBucket(inv.getItem(slot + 1))) {
                    emptyBucket.type = Material.WATER_BUCKET
                    return true
                }
            }
        } else if (slotType == CONTAINER) {
            val height = inv.size / width

            val horizontal = slot % width
            val vertical = slot / width

            val match = { offset: Int ->
                if (
                    isWaterBucket(inv.getItem(slot - offset)) &&
                    isWaterBucket(inv.getItem(slot + offset))
                ) {
                    emptyBucket.type = Material.WATER_BUCKET
                    true
                } else false
            }

            if (horizontal == 0 || horizontal == (width - 1)) {
                if (vertical == 0 || vertical == (height - 1)) return false
                if (match(width)) return true
            }

            if ((vertical == 0 || vertical == height - 1) && match(1)) return true
            if (match(1)) return true
            if (match(width)) return true
        }
        return false
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inv = event.clickedInventory ?: return

        val cursor = event.cursor
        if (cursor?.type == Material.BUCKET) {
            checkInfiniteWater(inv, event.slotType, event.slot, cursor)
        } else {
            runBukkit { genericHandler(inv) }
        }
    }

    @EventHandler
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        val inv = event.player.inventory
        if (event.bucket == Material.WATER_BUCKET) {
           runBukkit { checkInfiniteWater(inv, QUICKBAR, inv.heldItemSlot, inv.itemInMainHand) }
        }
    }

    @EventHandler
    fun onPlayerSwapHands(event: PlayerSwapHandItemsEvent) {
        val inv = event.player.inventory
        val item = inv.itemInMainHand
        if (item.type == Material.BUCKET) {
            checkInfiniteWater(inv, QUICKBAR, inv.heldItemSlot, item)
        } else if(item.type == Material.WATER_BUCKET) {
            runBukkit { genericHandler(inv) }
        }
    }

    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        val entity = event.entity
        val types = listOf(Material.BUCKET, Material.WATER_BUCKET)
        if(entity is Player && event.item.itemStack.type in types) {
            runBukkit { genericHandler(entity.inventory) }
        }
    }

    private fun genericHandler(inventory: Inventory) {
        for (i in 0 until inventory.size) {
            val item = inventory.getItem(i)
            if (item?.type == Material.BUCKET) {
                if (inventory.type == PLAYER) {
                    if (i < 9) {
                        checkInfiniteWater(inventory, QUICKBAR, i, item)
                        continue
                    } else if (i == 36) break
                }
                checkInfiniteWater(inventory, CONTAINER, i, item)
            }
        }
    }
}