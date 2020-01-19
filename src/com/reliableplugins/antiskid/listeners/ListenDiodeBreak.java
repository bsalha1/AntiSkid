/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Diode;

import java.util.Map;
import java.util.Set;

public class ListenDiodeBreak implements Listener
{
    private AntiSkid plugin;

    public ListenDiodeBreak(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        Material material = block.getType();
        if(!material.equals(Material.DIODE_BLOCK_OFF) && !material.equals(Material.DIODE_BLOCK_ON) && !material.equals(Material.DIODE))
        {
            return;
        }


        Chunk chunk = block.getChunk();
        for(Map<Chunk, Set<Location>> chunkMap : plugin.diodes.values())
        {
            if(chunkMap.containsKey(chunk))
            {
                chunkMap.get(chunk).remove(block.getLocation());
            }
        }

    }
}
