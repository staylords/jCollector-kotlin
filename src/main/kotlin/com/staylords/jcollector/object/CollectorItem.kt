/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.`object`

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class CollectorItem() {

    lateinit var displayName: String
    lateinit var type: Material

    var unitPrice: Double = 0.0

    constructor(displayName: String, type: Material) : this() {
        this.displayName = displayName
        this.type = type
    }

    fun toItemStack(): ItemStack {
        return ItemStack(type)
    }

}