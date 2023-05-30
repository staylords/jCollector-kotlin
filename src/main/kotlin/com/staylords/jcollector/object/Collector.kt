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
import com.staylords.jcollector.hooks.impl.VaultHook
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.services.HookService
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
//@Serializable
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

    private fun sellItem(executor: Player, type: Material) {
        val collectorService: CollectorService = JCollector.instance.collectorService
        val hookService: HookService = JCollector.instance.hookService

        val item: CollectorItem = collectorService.getCollectorItem(type)!!

        val amountStored = storedItems[item]
        val gain: Double = amountStored?.times(collectorService.getItemPrice(item)) ?: 0.0

        val faction: Faction = Factions.getInstance().getFactionById(id)
        // FactionsUUID has no faction bank to store money there, so we deposit the amount
        val vaultHook: VaultHook = hookService.getVaultHook()
        vaultHook.economy?.depositPlayer(executor, gain)
        executor.sendMessage("${ChatColor.GREEN}You're now $$gain richer!")

        // Send a message and play a sound to all online faction members
        faction.onlinePlayers.forEach {
            it.sendMessage(
                "${ChatColor.GREEN}${ChatColor.BOLD}[Collector] ${ChatColor.GRAY}${executor.name} ${ChatColor.WHITE}withdrew ${ChatColor.DARK_GREEN}$$gain ${ChatColor.WHITE}by selling ${ChatColor.GRAY}${item.displayName}${ChatColor.WHITE}."
            )
            it.playSound(it.location, Sound.NOTE_PLING, 1.0f, 1.0f)
        }

        this.soldItemsCount = soldItemsCount + amountStored!!
        this.totalSalesAmount = totalSalesAmount + gain

        this.storedItems[item] = 0
    }

}