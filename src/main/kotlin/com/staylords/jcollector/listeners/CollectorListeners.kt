/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.listeners

import com.staylords.jcollector.JCollector
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.`object`.CollectorItem
import com.staylords.jcollector.services.CollectorService
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockGrowEvent

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class CollectorListeners : Listener {

    @EventHandler
    fun onBlockGrowEvent(event: BlockGrowEvent) {
        val collectorService: CollectorService = JCollector.instance.collectorService
        val collector: Collector = collectorService.getCollector(event.block.location) ?: return
        val blockType: Material = event.newState.type

        if (collector.canCollect(blockType)) {
            if (!event.isCancelled) {
                event.isCancelled = true
            }
            val collectorItem: CollectorItem = collectorService.getCollectorItem(blockType)!!
            collector.increment(collectorItem, 1)
        }
    }


}