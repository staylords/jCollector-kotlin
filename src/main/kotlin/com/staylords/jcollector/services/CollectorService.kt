/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.services

import com.bgsoftware.wildstacker.api.WildStackerAPI
import com.massivecraft.factions.*
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.hooks.impl.ShopGUIPlusHook
import com.staylords.jcollector.hooks.impl.WildStackerHook
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.`object`.CollectorItem
import net.brcdev.shopgui.ShopGuiPlusApi
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class CollectorService(private val plugin: JCollector) {

    val collectors: HashMap<String, Collector> = HashMap()

    val collectorItems: ArrayList<CollectorItem> = ArrayList()

    init {
        collectors.clear()
        collectorItems.clear()

        JCollector.asyncTimer(30L * 20L, 30L * 20L) {
            val start = System.currentTimeMillis()
            collectors.values.forEach { plugin.mongoService.saveCollector(it) }
            plugin.logger.info("Saved ${collectors.values.size} collectors in ${System.currentTimeMillis() - start}ms.")
        }
    }

    fun addCollector(collector: Collector) {
        collectors[collector.id] = collector
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

    fun getCollectorItem(name: String): CollectorItem? {
        return collectorItems.stream().filter { item -> item.displayName.equals(name, ignoreCase = true) }.findFirst()
            .orElse(null)
    }

    fun getItemPrice(item: CollectorItem): Double {
        val sgpHook: ShopGUIPlusHook? = (plugin.hookService.getHook("ShopGUIPlus") as ShopGUIPlusHook?)
        if (sgpHook != null && sgpHook.isEnabled()) {
            return ShopGuiPlusApi.getItemStackPriceSell(item.toItemStack())
        }
        return item.unitPrice
    }

    fun getEntitiesAmount(entity: LivingEntity): Int {
        val wildStackerHook: WildStackerHook? = (plugin.hookService.getHook("WildStacker") as WildStackerHook?)
        if (wildStackerHook != null && wildStackerHook.isEnabled()) {
            return WildStackerAPI.getEntityAmount(entity)
        }
        return 1
    }

    fun loadCollectors() {
        val mongoService: MongoService = plugin.mongoService

        // Load collectors from database
        try {
            mongoService.getCollectorsIds(callback = {
                plugin.logger.info("Found ${it.size} collectors in database.")
                it.forEach { id -> mongoService.loadCollector(id) }
            })
            plugin.logger.info("Successfully loaded all collectors!")
        } catch (e: Exception) {
            plugin.logger.severe("Failed to load collectors.")
            e.printStackTrace()
        }
    }

    fun loadItems() {
        val mongoService: MongoService = plugin.mongoService

        // Load items from database
        try {
            mongoService.getCollectorItemsType(callback = {
                plugin.logger.info("Found ${it.size} collector items in database.")
                it.forEach { id -> mongoService.loadCollectorItem(id) }
            })
            plugin.logger.info("Successfully loaded all collector items!")
        } catch (e: Exception) {
            plugin.logger.severe("Failed to load collector items.")
            e.printStackTrace()
        }
    }

}