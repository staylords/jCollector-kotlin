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
import com.jonahseguin.drink.annotation.Text
import com.massivecraft.factions.FPlayer
import com.massivecraft.factions.Faction
import com.massivecraft.factions.perms.Role
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.JCollectorConst
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.`object`.CollectorItem
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.services.MongoService
import com.staylords.jcollector.ui.CollectorBuyUI
import com.staylords.jcollector.ui.CollectorUI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class CollectorCommands {

    @Command(name = "", desc = "Buy or open your faction's collector", async = true)
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

    @Command(name = "item create", desc = "dev", usage = "<drop_type [id/name]> <name..>")
    @Require("jcollector.item.create")
    fun collectorItemCreateCommand(@Sender sender: Player, type: Material, @Text displayName: String) {
        val collectorService: CollectorService = JCollector.instance.collectorService

        if (collectorService.getCollectorItem(type) != null) {
            sender.sendMessage("${ChatColor.RED}An item for ${type.name} already exist.")
            return
        }

        val collectorItem = CollectorItem(displayName, type)
        collectorService.addCollectorItem(collectorItem)

        sender.sendMessage("${ChatColor.YELLOW}Successfully added item ${ChatColor.GREEN}${displayName}${ChatColor.YELLOW}: ${ChatColor.RED}${type.name} (${type.id})${ChatColor.YELLOW}.")
    }

    @Command(
        name = "item info",
        desc = "List information for the provided collector item",
        usage = "<item_type/name..>",
        async = true
    )
    @Require("jcollector.item.info")
    fun collectorItemInfoCommand(@Sender sender: Player, item: CollectorItem) {
        val toReturn = arrayOf(
            "${ChatColor.YELLOW}Display Name: ${ChatColor.GREEN}${item.displayName}",
            "${ChatColor.YELLOW}Type: ${ChatColor.RED}${item.type.name} (${item.type.id})",
            "${ChatColor.YELLOW}Unit price: ${ChatColor.GREEN}$${JCollectorConst.NUMBER_FORMAT.format(item.unitPrice)}"
        )

        sender.sendMessage(toReturn)
    }

    @Command(
        name = "item price",
        desc = "Set the unit price for the provided collector item",
        usage = "<item_type/name..> <price>",
        async = true
    )
    @Require("jcollector.item.price")
    fun collectorItemPriceCommand(@Sender sender: Player, item: CollectorItem, price: Double) {
        val mongoService: MongoService = JCollector.instance.mongoService

        item.unitPrice = price
        mongoService.saveCollectorItem(item)

        sender.sendMessage("${ChatColor.YELLOW}Successfully set the unit price to ${ChatColor.GREEN}$${JCollectorConst.NUMBER_FORMAT.format(price)} ${ChatColor.YELLOW}for ${ChatColor.RED}${item.type.name} (${item.type.id})${ChatColor.YELLOW}.")
    }

    @Command(name = "item delete", desc = "dev", usage = "<drop_type [id/name]>")
    @Require("jcollector.item.delete")
    fun collectorItemDeleteCommand(@Sender sender: Player, type: Material, @Text displayName: String) {
    }

    @Command(name = "save", desc = "dev")
    @Require("jcollector.item.delete")
    fun collectorsSaveCommand(@Sender sender: Player) {
        val collectorService: CollectorService = JCollector.instance.collectorService
        val mongoService: MongoService = JCollector.instance.mongoService

        collectorService.collectors.forEach { mongoService.saveCollector(it.value) }
        collectorService.collectorItems.forEach { mongoService.saveCollectorItem(it) }
    }

}