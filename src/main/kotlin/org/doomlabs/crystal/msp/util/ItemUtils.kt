package org.doomlabs.crystal.msp.util

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.inventory.ItemStack

fun ItemStack.name(): String {
    return PlainTextComponentSerializer.plainText().serialize(this.displayName())
}