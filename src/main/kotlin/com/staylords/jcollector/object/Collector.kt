/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.`object`

import com.massivecraft.factions.Faction
import com.massivecraft.factions.Factions
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.JCollectorConst
import com.staylords.jcollector.hooks.impl.VaultHook
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.services.HookService
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class Collector() {

    lateinit var id: String
    lateinit var storedItems: HashMap<CollectorItem, Int>

    var soldItemsCount: Int = 0
    var totalSalesAmount: Double = 0.0

    constructor(id: String) : this() {
        this.id = id
        this.storedItems = HashMap()

        initialize()
    }

    private fun initialize() {
        val collectorService: CollectorService = JCollector.instance.collectorService

        collectorService.collectorItems.forEach {
            storedItems[it] = 0
        }
    }

    fun canCollect(type: Material): Boolean = storedItems.any { it.key.type == type }

    fun increment(item: CollectorItem, value: Int) {
        val valueStored: Int = storedItems[item]!!
        storedItems[item] = valueStored + value
    }

    fun sell(vararg items: CollectorItem, sender: Player) {
        val collectorService: CollectorService = JCollector.instance.collectorService
        val hookService: HookService = JCollector.instance.hookService
        val vault: VaultHook = hookService.getVaultHook()

        val gain = items.sumByDouble {
            storedItems[it]?.times(collectorService.getItemPrice(it)) ?: 0.0
        }

        val count = items.sumBy { storedItems[it] ?: 0 }

        vault.economy?.depositPlayer(sender, gain)
        sender.sendMessage("${ChatColor.YELLOW}You're now ${ChatColor.GREEN}$${JCollectorConst.NUMBER_FORMAT.format(gain)} ${ChatColor.YELLOW}richer!")

        val faction: Faction = Factions.getInstance().getFactionById(id)

        /**
         * Send a message to each faction online player
         */
        val separator = "${ChatColor.YELLOW}, ${ChatColor.RED}"
        val lastSeparator = "${ChatColor.YELLOW}and${ChatColor.RED}"

        val displayNames = items.filter { storedItems[it]!! > 0 }.map { it.type.name.toLowerCase().replace("_", " ") }
        val result = when {
            displayNames.size <= 1 -> displayNames.joinToString(separator = separator)
            else -> "${
                displayNames.dropLast(1).joinToString(separator = separator)
            } $lastSeparator ${displayNames.last()}"
        }

        val toReturn = "${ChatColor.GREEN}${sender.name} ${ChatColor.YELLOW}withdrew ${ChatColor.GREEN}$${
            JCollectorConst.NUMBER_FORMAT.format(gain)
        } ${ChatColor.YELLOW}by selling ${ChatColor.GREEN}${
            JCollectorConst.NUMBER_FORMAT.format(count)
        } ${ChatColor.YELLOW}units worth of ${ChatColor.RED}${result}${ChatColor.YELLOW}!"

        faction.onlinePlayers.forEach { it.sendMessage(toReturn) }

        /**
         * Updates collector statistics
         */
        items.forEach {
            storedItems[it] = 0
        }

        soldItemsCount += count
        totalSalesAmount += gain
    }

}