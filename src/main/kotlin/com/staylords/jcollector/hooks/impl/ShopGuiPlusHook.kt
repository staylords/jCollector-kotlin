/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.hooks.impl

import com.staylords.jcollector.hooks.Hook

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class ShopGuiPlusHook : Hook {

    var shopGuiPlusImplementation: Boolean = false

    override fun run() {
        shopGuiPlusImplementation = true
    }

    override fun isEnabled(): Boolean {
        return shopGuiPlusImplementation
    }


}