/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.services

import com.massivecraft.factions.*
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.hooks.impl.ShopGuiPlusHook
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.`object`.CollectorItem
import net.brcdev.shopgui.ShopGuiPlusApi
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class CollectorService(private val plugin: JCollector) {

    private val collectors: HashMap<String, Collector> = HashMap()

    val collectorItems: ArrayList<CollectorItem> = ArrayList()

    init {
        collectors.clear()
        collectorItems.clear()

        loadItems()
        loadCollectors()
    }

    fun addCollector(collector: Collector) {
        collectors[collector.id] = collector
        collector.initialize()
    }

    fun getCollector(id: String): Collector? {
        return collectors[id]
    }

    fun getCollector(player: Player): Collector? {
        val factionPlayer: FPlayer = FPlayers.getInstance().getByPlayer(player)
        return collectors[factionPlayer.faction.id]
    }

    fun getCollector(location: Location): Collector? {
        val faction: Faction = Board.getInstance().getFactionAt(FLocation(location.chunk))
        return collectors[faction.id]
    }

    fun addCollectorItem(item: CollectorItem) {
        collectorItems.add(item)
        collectors.forEach {
            val collector: Collector = it.value
            if (!collector.storedItems.containsKey(item)) {
                collector.storedItems[item] = 0
            }
        }
    }

    fun getCollectorItem(type: Material): CollectorItem? {
        return collectorItems.stream().filter { item -> item.type == type }.findFirst().orElse(null)
    }

    fun getItemPrice(item: CollectorItem): Double {
        val sgpHook: ShopGuiPlusHook? = (plugin.hookService.getHook("ShopGUIPlus") as ShopGuiPlusHook?)
        if (sgpHook != null && sgpHook.isEnabled()) {
            return ShopGuiPlusApi.getItemStackPriceSell(item.toItemStack())
        }
        return 0.0
    }

    private fun loadCollectors() {
        // Load collectors from database
    }

    private fun loadItems() {
        // Load items from database
    }

}