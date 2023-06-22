package com.staylords.jcollector

import java.text.NumberFormat
import java.util.*

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
object JCollectorConst {

    val NUMBER_FORMAT: NumberFormat = NumberFormat.getInstance(Locale.ITALY)

    const val COLLECTOR_COST: Double = 500000.0

    const val MONGO_URI = "mongodb://localhost:27017"
    const val MONGO_DATABASE = "jCollector"

    const val WILD_STACKER_IMPLEMENTATION = true
    const val SHOP_GUI_PLUS_IMPLEMENTATION = false

}