/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.listeners

import com.bgsoftware.wildstacker.api.WildStackerAPI
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.hooks.impl.WildStackerHook
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.`object`.CollectorItem
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.services.HookService
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class CollectorListener : Listener {

    @EventHandler
    fun onBlockGrowEvent(event: BlockGrowEvent) {
        val collectorService: CollectorService = JCollector.instance.collectorService
        val collector: Collector = collectorService.getCollector(event.block.location) ?: return
        val blockType: Material = event.newState.type

        if (collector.canCollect(blockType)) {
            if (!event.isCancelled) {
                event.isCancelled = true
            }
            val item: CollectorItem = collectorService.getCollectorItem(blockType)!!
            collector.increment(item, 1)
        }
    }

    @EventHandler
    fun onEntityDeathEvent(event: EntityDeathEvent) {
        val collectorService: CollectorService = JCollector.instance.collectorService

        val collector: Collector = collectorService.getCollector(event.entity.location) ?: return
        val drops: ArrayList<ItemStack> = ArrayList()

        event.drops.filter { collector.canCollect(it.type) }.forEach {
            val item: CollectorItem = collectorService.getCollectorItem(it.type)!!
            val amount: Int = it.amount.times(collectorService.getEntitiesAmount(event.entity))
            collector.increment(item, amount)

            drops.add(it)
        }

        event.drops.removeIf { drops.contains(it) }
    }

}