/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.services

import com.staylords.jcollector.JCollector
import com.staylords.jcollector.hooks.Hook
import com.staylords.jcollector.hooks.impl.ShopGuiPlusHook
import com.staylords.jcollector.hooks.impl.VaultHook
import com.staylords.jcollector.hooks.impl.WildStackerHook

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class HookService(plugin: JCollector) {

    private val hooks: HashMap<String, Hook> = HashMap()

    /**
     * Vault and FactionsUUID are NOT optional plugins as jCollector depends on them to work.
     */
    init {
        register("Vault", VaultHook())

        if (plugin.server.pluginManager.getPlugin("ShopGUIPlus") != null) {
            register("ShopGUIPlus", ShopGuiPlusHook())
        }

        if (plugin.server.pluginManager.getPlugin("WildStacker") != null) {
            register("WildStacker", WildStackerHook())
        }
    }

    private fun register(name: String, hook: Hook) {
        hooks[name] = hook
        hook.run()
    }

    fun getHook(name: String): Hook? {
        return hooks[name]
    }

    fun getVaultHook(): VaultHook {
        return (getHook("Vault") as VaultHook)
    }

}