/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.ui

import com.staylords.jcollector.JCollector
import com.staylords.jcollector.JCollectorConst
import com.staylords.jcollector.hooks.impl.VaultHook
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.services.HookService
import com.staylords.jcollector.ui.utils.ItemBuilder
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import fr.minuskube.inv.content.SlotPos
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class CollectorUI() : InventoryProvider {

    private lateinit var collector: Collector

    constructor(collector: Collector) : this() {
        this.collector = collector
    }

    fun getInventory(): SmartInventory {
        return SmartInventory.builder()
            .manager(JCollector.instance.inventoryManager)
            .id("collector")
            .provider(this)
            .size(5, 9)
            .title("Collector #" + collector.id)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        contents.fillBorders(ClickableItem.empty(ItemStack(Material.STAINED_GLASS_PANE)))
        contents.newIterator("animation", SlotIterator.Type.HORIZONTAL, 3, 0)
    }

    override fun update(player: Player, contents: InventoryContents) {
        val state: Int = contents.property("state", 0)
        contents.setProperty("state", state + 1)

        if (state % 5 != 0) {
            return
        }

        val glass = ItemStack(Material.STAINED_GLASS_PANE, 1, 7)
        var iterator = contents.iterator("animation").get()
        iterator.allowOverride(true)

        iterator.blacklist(SlotPos.of(3, 3))
        iterator.blacklist(SlotPos.of(3, 4))
        iterator.blacklist(SlotPos.of(3, 5))

        if (iterator.column() >= 7 && iterator.ended()) {
            iterator = contents.newIterator("animation", SlotIterator.Type.HORIZONTAL, 3, 0)
            contents.fillRow(3, ClickableItem.empty(null))
        }

        iterator.next()
        iterator.set(ClickableItem.empty(ItemStack(Material.STAINED_GLASS_PANE, 1, 5)))

        contents.fillBorders(ClickableItem.empty(glass))

        // UI design
        val hookService: HookService = JCollector.instance.hookService
        val vaultHook: VaultHook = hookService.getVaultHook()

        contents.set(3, 4, ClickableItem.of(
            ItemBuilder(Material.HOPPER).name("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Collector")
                .setLore(
                    listOf(
                        "${ChatColor.DARK_GRAY}ID: ${collector.id}",
                        "",
                        "${ChatColor.WHITE}Status: ${ChatColor.GRAY}Enabled",
                        "${ChatColor.WHITE}Sold Items: ${ChatColor.GRAY}${JCollectorConst.NUMBER_FORMAT.format(collector.soldItemsCount)}",
                        "${ChatColor.WHITE}Total Sales: ${ChatColor.GRAY}$${JCollectorConst.NUMBER_FORMAT.format(collector.totalSalesAmount)}",
                        "",
                        "${ChatColor.GREEN}You have ${ChatColor.DARK_GREEN}0 ${ChatColor.GREEN}items available to sell for ${ChatColor.DARK_GREEN}$0.0",
                        " ${ChatColor.GREEN}-> Click here to sell everything",
                    )
                ).build()
        ) {
            //if () {
//
            //          }
        })
    }
}