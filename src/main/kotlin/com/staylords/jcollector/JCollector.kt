/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector

import com.jonahseguin.drink.CommandService
import com.jonahseguin.drink.Drink
import com.jonahseguin.drink.annotation.Sender
import com.massivecraft.factions.FPlayer
import com.staylords.jcollector.commands.CollectorCommands
import com.staylords.jcollector.commands.providers.CollectorItemProvider
import com.staylords.jcollector.commands.providers.FactionPlayerProvider
import com.staylords.jcollector.`object`.CollectorItem
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.services.HookService
import fr.minuskube.inv.InventoryManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class JCollector : JavaPlugin() {

    lateinit var collectorService: CollectorService
    lateinit var hookService: HookService
    lateinit var inventoryManager: InventoryManager

    override fun onEnable() {
        instance = this

        collectorService = CollectorService(this)
        hookService = HookService(this)

        registerCommands()

        // Initialize SmartInvs
        inventoryManager = InventoryManager(this)
        inventoryManager.init()
    }

    private fun registerCommands() {
        val commandFramework: CommandService = Drink.get(this)

        // Register the providers first
        commandFramework.bind(CollectorItem::class.java).toProvider(CollectorItemProvider())
        commandFramework.bind(FPlayer::class.java).annotatedWith(Sender::class.java).toProvider(FactionPlayerProvider())

        // Register the commands
        commandFramework.register(CollectorCommands(), "collector", "", "collector")

        commandFramework.registerCommands()
    }

    companion object {

        lateinit var instance: JCollector

    }

}