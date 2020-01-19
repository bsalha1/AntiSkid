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
        // If not a diode, return
        if(!(event.getBlock() instanceof Diode)) return;

        Chunk chunk = event.getBlock().getChunk();
        for (Map<Chunk, Set<Location>> chunkSetMap : plugin.diodes.values())
        {
            // If chunk is protected, add the new diode to the list
            if(chunkSetMap.containsKey(chunk))
            {
                chunkSetMap.get(chunk).add(event.getBlock().getLocation());
            }
        }
    }
}
