/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector

import com.staylords.jcollector.services.CollectorService
import org.bukkit.plugin.java.JavaPlugin

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class JCollector : JavaPlugin() {

    lateinit var collectorService: CollectorService

    override fun onEnable() {
        instance = this

        collectorService = CollectorService(this)
    }


    companion object {

        lateinit var instance: JCollector

    }

}