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
import com.staylords.jcollector.commands.providers.MaterialProvider
import com.staylords.jcollector.listeners.CollectorListener
import com.staylords.jcollector.listeners.FactionsListener
import com.staylords.jcollector.`object`.CollectorItem
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.services.HookService
import com.staylords.jcollector.services.MongoService
import fr.minuskube.inv.InventoryManager
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class JCollector : JavaPlugin() {

    lateinit var mongoService: MongoService
    lateinit var collectorService: CollectorService
    lateinit var hookService: HookService

    lateinit var inventoryManager: InventoryManager

    override fun onEnable() {
        instance = this

        collectorService = CollectorService(this)
        mongoService = MongoService(this)
        hookService = HookService(this)

        collectorService.loadItems()
        collectorService.loadCollectors()

        registerCommands()

        server.pluginManager.registerEvents(CollectorListener(), this)
        server.pluginManager.registerEvents(FactionsListener(), this)

        /**
         * Initialize InventoryManager class (SmartInvs)
         * @see fr.minuskube.inv.InventoryManager
         */
        inventoryManager = InventoryManager(this)
        inventoryManager.init()
    }

    override fun onDisable() {
        collectorService.collectors.values.forEach { mongoService.saveCollector(it)  }

        mongoService.close()
    }

    /**
     * @see com.jonahseguin.drink.Drink
     */
    private fun registerCommands() {
        val commandFramework: CommandService = Drink.get(this)

        // Register the providers first
        commandFramework.bind(CollectorItem::class.java).toProvider(CollectorItemProvider())
        commandFramework.bind(Material::class.java).toProvider(MaterialProvider())
        commandFramework.bind(FPlayer::class.java).annotatedWith(Sender::class.java).toProvider(FactionPlayerProvider())

        // Register the commands
        commandFramework.register(CollectorCommands(), "collector", "", "collector")

        commandFramework.registerCommands()
    }

    companion object {

        lateinit var instance: JCollector

        @JvmStatic
        fun asyncTimer(delay: Long, interval: Long, lambda: () -> Unit): BukkitTask {
            return instance.server.scheduler.runTaskTimerAsynchronously(instance, {
                lambda.invoke()
            }, delay, interval)
        }
    }

}