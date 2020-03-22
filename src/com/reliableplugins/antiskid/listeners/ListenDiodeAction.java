package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
//import com.reliableplugins.antiskid.hook.PlotSquaredHook;
import com.reliableplugins.antiskid.task.ATask;
import com.reliableplugins.antiskid.hook.FactionHook;
import com.reliableplugins.antiskid.type.Whitelist;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ListenDiodeAction implements Listener
{
    private AntiSkid plugin;

    public ListenDiodeAction(AntiSkid plugin)
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
                        new ATask(plugin, 15)
                        {
                            @Override
                            public void run()
                            {
                                plugin.getNMS().broadcastBlockChangePacket(plugin.getReplacer(), loc, whitelist.getUUIDs());
                            }
                        };
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDiodeBreak(BlockBreakEvent event)
    {
        Material material = event.getBlock().getType();
        if((!material.equals(Material.DIODE_BLOCK_OFF)
                && !material.equals(Material.DIODE_BLOCK_ON)
                && !material.equals(Material.DIODE))
                || !plugin.cache.isProtected(event.getBlock().getChunk()))
        {
            return;
        }

        World world = event.getBlock().getWorld();
        Location location = event.getBlock().getLocation();

        if(plugin.config.factionsWorlds.contains(world))
        {
            if(FactionHook.canBuild(event.getPlayer(), location.getChunk()))
            {
                plugin.cache.unprotectLocation(location);
            }
        }
//        else if(plugin.getPlotsWorlds().contains(world))
//        {
//            if(PlotSquaredHook.isAdded(event.getPlayer(), location))
//            {
//                plugin.cache.unprotectLocation(location);
//            }
//        }
        else
        {
            plugin.cache.unprotectLocation(location);
        }
    }
}
