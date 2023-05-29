/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.commands

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import com.massivecraft.factions.FPlayer
import com.massivecraft.factions.Faction
import com.massivecraft.factions.perms.Role
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.`object`.CollectorItem
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.ui.CollectorBuyUI
import com.staylords.jcollector.ui.CollectorUI
import net.minecraft.server.v1_8_R3.BlockCactus
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class CollectorCommands {

    @Command(name = "", aliases = [], desc = "Main collector command - Use/Buy")
    @Require("jcollector.main")
    fun collectorCommand(@Sender player: FPlayer) {
        val collectorService: CollectorService = JCollector.instance.collectorService

        val faction: Faction = player.faction
        val collector: Collector? = collectorService.getCollector(faction.id)

        if (collector == null) {
            if (player.role.isAtLeast(Role.MODERATOR)) {
                // Open collector_buy gui
                CollectorBuyUI.ui.open(player.player)
            } else {
                player.sendMessage("${ChatColor.RED}You must be at least faction moderator to buy a collector.")
            }
            return
        }

        if (player.role.isAtMost(Role.NORMAL)) {
            player.sendMessage("${ChatColor.RED}You must be at least faction moderator to open your faction's collector.")
            return
        }

        // Open collector gui
        CollectorUI(collector).getInventory().open(player.player)
    }

    @Command(name = "item add", desc = "dev")
    @Require("jcollector.item.add")
    fun collectorItemAddCommand(@Sender sender: Player) {
        var collectorItem = CollectorItem()

    }

}