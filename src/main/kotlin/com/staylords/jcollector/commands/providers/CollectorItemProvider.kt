/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.commands.providers

import com.jonahseguin.drink.argument.CommandArg
import com.jonahseguin.drink.exception.CommandExitMessage
import com.jonahseguin.drink.parametric.DrinkProvider
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.`object`.CollectorItem
import com.staylords.jcollector.services.CollectorService
import org.bukkit.Material
import java.util.stream.Collectors

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class CollectorItemProvider : DrinkProvider<CollectorItem>() {
    override fun doesConsumeArgument(): Boolean {
        return true
    }

    override fun isAsync(): Boolean {
        return true
    }

    override fun provide(arg: CommandArg, annotations: MutableList<out Annotation>): CollectorItem {
        val item: String = arg.get()
        val collectorService: CollectorService = JCollector.instance.collectorService

        var collectorItem: CollectorItem? = null

        if (collectorService.getCollectorItem(item) != null) {
            collectorItem = collectorService.getCollectorItem(item)!!
        }

        if (Material.matchMaterial(item) != null && collectorService.getCollectorItem(Material.matchMaterial(item)) != null) {
            collectorItem = collectorService.getCollectorItem(Material.matchMaterial(item))!!
        }

        if (collectorItem == null) {
            throw CommandExitMessage("No collector item found for '${item}'.")
        }

        return collectorItem
    }

    override fun argumentDescription(): String {
        return "drop type"
    }

    override fun getSuggestions(prefix: String): List<String> {
        val finalPrefix: String = prefix.toLowerCase()
        val collectorService: CollectorService = JCollector.instance.collectorService

        return collectorService.collectorItems.map { collectorItem -> collectorItem.displayName.toLowerCase() }
            .filter { s -> finalPrefix.isEmpty() || s.startsWith(finalPrefix) }.stream().collect(Collectors.toList())
    }


}