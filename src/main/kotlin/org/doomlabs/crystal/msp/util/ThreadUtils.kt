package org.doomlabs.crystal.msp.util

import org.bukkit.scheduler.BukkitRunnable
import org.doomlabs.crystal.msp.Crystal

fun <T> runBukkit(action: () -> T) {
    object : BukkitRunnable() {
        override fun run() {
            action()
        }
    }.runTask(Crystal.instance)
}