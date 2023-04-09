package org.doomlabs.crystal.msp.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.inventory.ItemStack

fun Component.text(): String {
    return PlainTextComponentSerializer.plainText().serialize(this)
}

fun ItemStack.name(): String {
    return this.displayName().text()
}
