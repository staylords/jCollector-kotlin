/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.ui

import com.staylords.jcollector.JCollector
import com.staylords.jcollector.JCollectorConst
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.ui.utils.ItemBuilder
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import fr.minuskube.inv.content.SlotPos
import net.md_5.bungee.api.ChatColor
import org.apache.commons.lang.WordUtils
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
        return SmartInventory.builder().manager(JCollector.instance.inventoryManager).id("collector").provider(this)
            .size(5, 9).title("Collector #" + collector.id).build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        contents.fillBorders(ClickableItem.empty(ItemStack(Material.STAINED_GLASS_PANE)))
        contents.newIterator("animation", SlotIterator.Type.HORIZONTAL, 3, 0)
    }

    /**
     * Little animation I made to show the collector status
     */
    private fun animate(contents: InventoryContents) {
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
    }

    private fun collector(contents: InventoryContents, player: Player) {
        val collectorService: CollectorService = JCollector.instance.collectorService

        val count = collector.storedItems.values.sum()
        val gain = collector.storedItems.keys.sumByDouble {
            collector.storedItems[it]?.times(collectorService.getItemPrice(it)) ?: 0.0
        }

        val lore = listOf(
            "${ChatColor.DARK_GRAY}ID: ${collector.id}",
            "",
            "${ChatColor.GRAY}Items Sold: ${ChatColor.YELLOW}${JCollectorConst.NUMBER_FORMAT.format(collector.soldItemsCount)}",
            "${ChatColor.GRAY}Total Sales: ${ChatColor.GOLD}$${JCollectorConst.NUMBER_FORMAT.format(collector.totalSalesAmount)}",
            "",
            "${ChatColor.GRAY}Status: ${ChatColor.GREEN}Working",
            "",
            "${ChatColor.GRAY}Item Units:",
            "${ChatColor.YELLOW}  Available: ${ChatColor.WHITE}${JCollectorConst.NUMBER_FORMAT.format(count)}",
            "${ChatColor.YELLOW}  Gain: ${ChatColor.WHITE}$${JCollectorConst.NUMBER_FORMAT.format(gain)}",
            "",
            "${ChatColor.YELLOW}Click to sell all!"
        )

        contents.set(3, 4, ClickableItem.of(
            ItemBuilder(Material.HOPPER).name("${ChatColor.GREEN}Collector").setLore(lore).build()
        ) {
            if (count > 0) collector.sell(
                *collector.storedItems.entries.map { it.key }.toTypedArray(),
                sender = player
            )
        })
    }

    private fun collectorItems(contents: InventoryContents, player: Player) {
        val collectorService: CollectorService = JCollector.instance.collectorService
        val startRow = 1
        val startColumn = 1
        val endRow = 2
        val endColumn = 7
        var currentRow = startRow
        var currentColumn = startColumn

        for ((item, quantity) in collector.storedItems.entries.sortedByDescending { it.value }) {
            val itemPrice = JCollectorConst.NUMBER_FORMAT.format(collectorService.getItemPrice(item))
            val displayName = "${ChatColor.GREEN}${item.displayName}"
            val drop = item.type.name.replace('_', ' ')
            val availableQuantity = JCollectorConst.NUMBER_FORMAT.format(quantity)
            val gain = JCollectorConst.NUMBER_FORMAT.format(quantity * collectorService.getItemPrice(item))

            val lore = listOf(
                "${ChatColor.DARK_GRAY}DROP: $drop",
                "",
                "${ChatColor.GRAY}Unit price: ${ChatColor.GREEN}$$itemPrice",
                "",
                "${ChatColor.GRAY}${WordUtils.capitalize(drop.toLowerCase())} Units:",
                "${ChatColor.YELLOW}  Available: ${ChatColor.WHITE}$availableQuantity",
                "${ChatColor.YELLOW}  Gain: ${ChatColor.WHITE}$$gain",
                "",
                "${ChatColor.YELLOW}Click to sell!"
            )

            val clickableItem = ClickableItem.of(
                ItemBuilder(item.type).name(displayName).setLore(lore).build()
            ) { if (quantity > 0) collector.sell(item, sender = player) }

            contents.set(SlotPos.of(currentRow, currentColumn), clickableItem)

            if (++currentColumn > endColumn) {
                currentColumn = startColumn
                if (++currentRow > endRow) break
            }
        }
    }

    override fun update(player: Player, contents: InventoryContents) {
        // Animation
        animate(contents)

        // UI design
        collector(contents, player)
        collectorItems(contents, player)
    }
}