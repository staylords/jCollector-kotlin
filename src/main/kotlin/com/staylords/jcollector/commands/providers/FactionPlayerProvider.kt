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
import com.massivecraft.factions.FPlayer
import com.massivecraft.factions.FPlayers
import javax.annotation.Nullable

/**
 * @project jCollector-kotlin
 *
 * @date 29/05/2023
 * @author me@staylords.com
 */
class FactionPlayerProvider : DrinkProvider<FPlayer>() {
    override fun doesConsumeArgument(): Boolean {
        return false
    }

    override fun isAsync(): Boolean {
        return true
    }

    @Nullable
    override fun provide(arg: CommandArg, annotations: MutableList<out Annotation>): FPlayer {
        val factionPlayer: FPlayer = FPlayers.getInstance().getByPlayer(arg.senderAsPlayer)
        if (!factionPlayer.hasFaction() || factionPlayer.faction.isWilderness) {
            throw CommandExitMessage("You must be in a faction to execute this command.")
        }

        return factionPlayer
    }

    override fun argumentDescription(): String {
        return "Return faction player"
    }


}