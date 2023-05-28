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
import org.bukkit.Material

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class Collector() {

    private lateinit var id: String
    private lateinit var storedItems: HashMap<CollectorItem, Int>

    private var soldItemsCount: Int = 0
    private var totalSalesAmount: Double = 0.0

    constructor(id: String) : this() {
        this.id = id
        this.storedItems = HashMap()
    }

    private fun sellItem(type: Material) {
        val item: CollectorItem = JCollector.instance.collectorService.getCollectorItem(type) ?: return

        val amountStored = storedItems[item]
        val gain = amountStored?.times(getPriceByMaterial(type))

        val faction: Faction = Factions.getInstance().getFactionById(id)
        //Make an actual deposit
        //Send a message
        //Play a sound

        this.soldItemsCount = soldItemsCount + amountStored!!
        this.totalSalesAmount = totalSalesAmount + gain!!

        this.storedItems[item] = 0
    }

    private fun getPriceByMaterial(type: Material): Double {
        return 1.0
    }

}