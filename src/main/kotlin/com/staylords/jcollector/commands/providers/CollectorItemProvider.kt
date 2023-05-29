/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.commands.providers

import com.jonahseguin.drink.argument.CommandArg
import com.jonahseguin.drink.parametric.DrinkProvider
import com.staylords.jcollector.`object`.CollectorItem

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

    override fun provide(arg: CommandArg, annotations: MutableList<out Annotation>): CollectorItem? {
        TODO("Not yet implemented")
    }

    override fun argumentDescription(): String {
        TODO("Not yet implemented")
    }


}