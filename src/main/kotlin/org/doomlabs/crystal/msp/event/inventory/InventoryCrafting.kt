package org.doomlabs.crystal.msp.event.inventory

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.advancement.Advancement
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
import org.doomlabs.crystal.msp.Crystal
import org.doomlabs.crystal.msp.data.recipe.InventoryCraftingRecipe
import org.doomlabs.crystal.msp.util.runBukkit
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Code to allow crafting inside the inventory
 * as a new concept of crafting.
 */
object InventoryCrafting : Listener {
    private val recipes = mutableMapOf<String, InventoryCraftingRecipe>()

    init {
        val inventoryPath = "/resources/recipes/inventory"
        this.javaClass.classLoader.getResourceAsStream(inventoryPath).use { folder ->
            if (folder != null) {
                val folderStream = InputStreamReader(folder, StandardCharsets.UTF_8)
                BufferedReader(folderStream).lines().forEach {
                    if (it.endsWith(".json")) {
                        val fileStream = this.javaClass.classLoader.getResourceAsStream("$inventoryPath/$it")
                        val data = fileStream!!.readAllBytes()!!.toString(Charsets.UTF_8)
                        val recipe = Json.decodeFromString<InventoryCraftingRecipe>(data)
                        recipes[it.substring(0, it.length - 5)] = recipe
                    } else {
                        Crystal.logger.warning("Found a file besides `.json` among recipes.")
                    }
                }
            } else {
                Crystal.logger.warning("Could not load custom recipes.")
            }
        }
        Crystal.logger.info("Loaded ${recipes.size} inventory recipes.")
    }

    // Award on any/all crafts
    private val rootAchievement: Advancement by lazy {
        Bukkit.getAdvancement(NamespacedKey("crystal", "root"))!!
    }

    // CT:
    // CT:
    // CT:
    // QB:
    private fun checkInventoryCrafting(inv: Inventory, slotType: SlotType, slot: Int, item: ItemStack): InventoryCraftingRecipe? {
        val legalInventories = listOf(BARREL, CHEST, PLAYER, ENDER_CHEST, SHULKER_BOX)
        val legalSlots = listOf(CONTAINER, QUICKBAR)
        if (inv.type !in legalInventories || slotType !in legalSlots) return null

        Crystal.logger.info("ST: $slotType, S: $slot, IT: $item")

        return null
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inv = event.clickedInventory ?: return

        val cursor = event.cursor
        if (cursor?.type == Material.BUCKET) {
            checkInventoryCrafting(inv, event.slotType, event.slot, cursor)
        } else {
            runBukkit { genericHandler(inv) }
        }
    }

    @EventHandler
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        val inv = event.player.inventory
        if (event.bucket == Material.WATER_BUCKET) {
           runBukkit { checkInventoryCrafting(inv, QUICKBAR, inv.heldItemSlot, inv.itemInMainHand) }
        }
    }

    @EventHandler
    fun onPlayerSwapHands(event: PlayerSwapHandItemsEvent) {
        val inv = event.player.inventory
        val item = inv.itemInMainHand
        if (item.type == Material.BUCKET) {
            checkInventoryCrafting(inv, QUICKBAR, inv.heldItemSlot, item)
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
                        checkInventoryCrafting(inventory, QUICKBAR, i, item)
                        continue
                    } else if (i == 36) break
                }
                checkInventoryCrafting(inventory, CONTAINER, i, item)
            }
        }
    }
}