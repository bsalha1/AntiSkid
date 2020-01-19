/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Diode;

import java.util.Map;
import java.util.Set;

public class ListenDiodePlace implements Listener
{
    private AntiSkid plugin;

    public ListenDiodePlace(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(!(event.getBlock() instanceof Diode)) return;

        Chunk chunk = event.getBlock().getChunk();
        try
        {
            plugin.lock.acquire();
        }
        catch(Exception ignored) { }
        for (Map<Chunk, Set<Location>> chunkSetMap : plugin.diodes.values())
        {
            if(chunkSetMap.containsKey(chunk))
            {
                chunkSetMap.get(chunk).add(event.getBlock().getLocation());
            }
        }
        plugin.lock.release();
    }
}
