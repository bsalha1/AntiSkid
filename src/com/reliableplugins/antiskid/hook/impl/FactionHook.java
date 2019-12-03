/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.hook.impl;

import com.massivecraft.factions.*;
import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.hook.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class FactionHook implements PluginHook<FactionHook>
{
    public static boolean canBuild(Player player, Chunk chunk)
    {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        FLocation fLoc = new FLocation(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        Faction faction = Board.getInstance().getFactionAt(fLoc);

        if(faction.isWilderness())
        {
            return false;
        }

        if(faction.equals(fPlayer.getFaction()))
        {
            return true;
        }
        return false;
    }

    @Override
    public FactionHook setup(Main main)
    {
        return this;
    }

    @Override
    public String getName()
    {
        return "Factions";
    }

}
