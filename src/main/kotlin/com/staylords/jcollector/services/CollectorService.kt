/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.services

import com.staylords.jcollector.JCollector
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.`object`.CollectorItem
import org.bukkit.Material

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class CollectorService(private val plugin: JCollector) {

    private val collectors: HashMap<String, Collector> = HashMap()

    private val collectorItems: ArrayList<CollectorItem> = ArrayList()

    init {
        collectors.clear()
        collectorItems.clear()

        loadItems()
        loadCollectors()
    }

    fun getCollector(id: String): Collector? {
        return collectors[id]
    }

    fun getCollectorItem(type: Material): CollectorItem? {
        return collectorItems.stream().filter { item -> item.type == type }.findFirst().orElse(null)
    }

    private fun loadCollectors() {

    }

    private fun loadItems() {

    }

}