package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractTask;
import com.reliableplugins.antiskid.type.Whitelist;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ListenDiodeOn implements Listener
{
    private AntiSkid plugin;

    public ListenDiodeOn(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockRedstoneEvent(BlockRedstoneEvent event)
    {
        if(!event.getBlock().getType().equals(Material.DIODE_BLOCK_ON))
        {
            return;
        }

        Location location = event.getBlock().getLocation();
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
        {
            Whitelist whitelist = plugin.whitelists.get(entry.getKey());
            for(Set<Location> locations : entry.getValue().values())
            {
                for(Location loc : locations)
                {
                    if(loc.equals(location))
                    {
                        new AbstractTask(plugin, 10)
                        {
                            @Override
                            public void run()
                            {
                                plugin.getNMS().broadcastBlockChangePacket(Material.CARPET, loc, whitelist.getUUIDs());
                            }
                        };
                    }
                }
            }
        }


    }
}
