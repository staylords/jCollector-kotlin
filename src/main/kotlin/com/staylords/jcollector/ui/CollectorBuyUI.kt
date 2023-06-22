/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.ui

import com.massivecraft.factions.FPlayer
import com.massivecraft.factions.FPlayers
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.JCollectorConst
import com.staylords.jcollector.hooks.impl.VaultHook
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.services.CollectorService
import com.staylords.jcollector.services.HookService
import com.staylords.jcollector.ui.utils.ItemBuilder
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class CollectorBuyUI : InventoryProvider {
    companion object {
        val ui: SmartInventory =
            SmartInventory.builder().manager(JCollector.instance.inventoryManager).id("collector_buy")
                .provider(CollectorBuyUI()).size(3, 9).title("Collector").build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        contents.fillBorders(ClickableItem.empty(ItemStack(Material.STAINED_GLASS_PANE)))
        val hookService: HookService = JCollector.instance.hookService

        val lore = ArrayList<String>()
        lore.add("${ChatColor.DARK_GRAY}Cost: $${JCollectorConst.NUMBER_FORMAT.format(JCollectorConst.COLLECTOR_COST)}")
        lore.add("")
        lore.add("${ChatColor.GRAY}Tired of harvesting cactus and")
        lore.add("${ChatColor.GRAY}sugar canes? Tired of having to kill")
        lore.add("${ChatColor.GRAY}all your squids, creepers and iron")
        lore.add("${ChatColor.GRAY}golems to obtain their loot?")
        lore.add("${ChatColor.GRAY}A collector stores every kind of")
        lore.add("${ChatColor.GRAY}mobs and farm drops for each")
        lore.add("${ChatColor.GRAY}chunk of your faction claim!")
        lore.add("")

        val vault: VaultHook = hookService.getVaultHook()
        if (vault.economy!!.getBalance(player) >= JCollectorConst.COLLECTOR_COST) {
            lore.add("${ChatColor.GREEN}Click here to buy it!")
        } else {
            lore.add("${ChatColor.RED}You don't have enough funds to buy it!")
        }

        val collectorItem: ItemStack =
            ItemBuilder(Material.HOPPER).name("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Buy a Collector").setLore(lore)
                .build()

        contents.set(1, 4, ClickableItem.of(collectorItem) {
            if (vault.economy!!.getBalance(player) >= JCollectorConst.COLLECTOR_COST) {
                vault.economy!!.withdrawPlayer(player, JCollectorConst.COLLECTOR_COST)

                val factionPlayer: FPlayer = FPlayers.getInstance().getByPlayer(player)
                val collectorService: CollectorService = JCollector.instance.collectorService

                val collector = Collector(factionPlayer.faction.id)
                collectorService.addCollector(collector)

                factionPlayer.faction.onlinePlayers.forEach {
                    it.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}[Collector] ${ChatColor.WHITE}Your faction bought a collector.")
                    it.playSound(it.location, Sound.NOTE_PLING, 1.0f, 1.0f)
                }

                player.closeInventory()

                // Open collector gui
                CollectorUI(collector).getInventory().open(player.player)
                return@of
            }

            player.sendMessage("${ChatColor.RED}You don't have enough money.")
            player.playSound(player.location, Sound.WOOD_CLICK, 1.0f, 1.0f)
        })
    }

    private var durability: Short = 5

    override fun update(player: Player, contents: InventoryContents) {
        val state: Int = contents.property("state", 0)
        contents.setProperty("state", state + 1)

        if (state % 20 != 0) {
            return
        }

        durability = if (durability == 5.toShort()) 13 else 5

        val glass = ItemStack(Material.STAINED_GLASS_PANE, 1, durability)
        contents.fillBorders(ClickableItem.empty(glass))
    }
}