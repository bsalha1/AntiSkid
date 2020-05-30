package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
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
    @EventHandler
    public void onBlockRedstoneEvent(BlockRedstoneEvent event)
    {
        if(!event.getBlock().getType().equals(Material.DIODE_BLOCK_ON))
        {
            return;
        }

        Location location = event.getBlock().getLocation();
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : AntiSkid.INSTANCE.diodes.entrySet())
        {
            Whitelist whitelist = AntiSkid.INSTANCE.whitelists.get(entry.getKey());
            for(Set<Location> locations : entry.getValue().values())
            {
                for(Location loc : locations)
                {
                    if(loc.equals(location))
                    {
                        new ATask(15)
                        {
                            @Override
                            public void run()
                            {
                                AntiSkid.INSTANCE.getNMS().broadcastBlockChangePacket(AntiSkid.INSTANCE.getReplacer(), loc, whitelist.getUUIDs());
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
                || !AntiSkid.INSTANCE.cache.isProtected(event.getBlock().getChunk()))
        {
            return;
        }

        World world = event.getBlock().getWorld();
        Location location = event.getBlock().getLocation();

        if(AntiSkid.INSTANCE.getMainConfig().factionsWorlds.contains(world))
        {
            if(FactionHook.canBuild(event.getPlayer(), location.getChunk()))
            {
                AntiSkid.INSTANCE.cache.unprotectLocation(location);
            }
        }
        else if(AntiSkid.INSTANCE.getMainConfig().plotsWorlds.contains(world))
        {
            if(AntiSkid.INSTANCE.getMainConfig().plotSquaredHook.isAdded(event.getPlayer(), location))
            {
                AntiSkid.INSTANCE.cache.unprotectLocation(location);
            }
        }
        else
        {
            AntiSkid.INSTANCE.cache.unprotectLocation(location);
        }
    }
}
