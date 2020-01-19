/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.listeners;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.utils.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class ListenUnclaim implements Listener
{
    private AntiSkid plugin;

    public ListenUnclaim(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onUnclaim(LandUnclaimEvent event)
    {
        Chunk chunk = event.getLocation().getChunk();
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
        {
            if(entry.getValue().containsKey(chunk))
            {
                plugin.diodes.get(entry.getKey()).remove(chunk);
                Util.reloadChunk(chunk);
                return;
            }
        }
    }

    @EventHandler
    public void onUnclaimAll(LandUnclaimAllEvent event)
    {
        Chunk chunk;
        for(FLocation floc : event.getFaction().getAllClaims())
        {
            chunk = floc.getChunk();
            for(Map<Chunk, Set<Location>> map : plugin.diodes.values())
            {
                map.remove(chunk);
                Util.reloadChunk(chunk);
            }
        }
    }
}
