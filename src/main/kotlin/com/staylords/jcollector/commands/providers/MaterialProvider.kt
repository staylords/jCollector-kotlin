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
import org.bukkit.Material
import java.util.stream.Collectors


/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class MaterialProvider : DrinkProvider<Material>() {
    override fun doesConsumeArgument(): Boolean {
        return true
    }

    override fun isAsync(): Boolean {
        return true
    }

    override fun provide(arg: CommandArg, annotations: MutableList<out Annotation>): Material {
        if (Material.matchMaterial(arg.get()) == null) {
            throw CommandExitMessage("No drop type found.")
        }

        return Material.matchMaterial(arg.get())
    }

    override fun argumentDescription(): String {
        return "drop type"
    }

    override fun getSuggestions(prefix: String): List<String> {
        val finalPrefix: String = prefix.toLowerCase()
        return Material.values().map { p -> p.name.toLowerCase() }
            .filter { s -> finalPrefix.isEmpty() || s.startsWith(finalPrefix) }.stream().collect(Collectors.toList())
    }

}