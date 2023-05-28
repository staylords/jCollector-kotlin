package com.staylords.jcollector

import org.bukkit.plugin.java.JavaPlugin

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class JCollector : JavaPlugin() {

    override fun onEnable() {
        instance = this
    }

    companion object {

        lateinit var instance: JCollector

    }
}