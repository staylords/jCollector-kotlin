/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.hooks.impl

import com.staylords.jcollector.JCollector
import com.staylords.jcollector.JCollectorConst
import com.staylords.jcollector.hooks.Hook

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class WildStackerHook : Hook {

    override fun run() {
        JCollector.instance.logger.info("WildStacker Hook: ${isEnabled()}")
    }

    override fun isEnabled(): Boolean {
        return JCollectorConst.WILD_STACKER_IMPLEMENTATION
    }
}