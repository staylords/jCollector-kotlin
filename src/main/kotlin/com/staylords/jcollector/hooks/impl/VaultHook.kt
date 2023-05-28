/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.hooks.impl

import com.staylords.jcollector.hooks.Hook
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.RegisteredServiceProvider

/**
 * @project jCollector-kotlin
 *
 * @date 28/05/2023
 * @author me@staylords.com
 */
class VaultHook : Hook {

    var economy: Economy? = null

    /**
     * Register Economy class from Vault
     * @see net.milkbowl.vault.economy.Economy
     */
    override fun run() {
        val registeredServiceProvider: RegisteredServiceProvider<Economy>? = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        if (registeredServiceProvider == null) {
            Bukkit.shutdown()
            return
        }

        economy = registeredServiceProvider.provider
    }

    override fun isEnabled(): Boolean {
        return economy != null
    }

}