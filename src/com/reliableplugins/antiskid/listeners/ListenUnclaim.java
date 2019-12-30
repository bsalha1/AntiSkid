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
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
        for(Map.Entry<UUID, Set<Chunk>> entry : plugin.chunkMap.entrySet())
        {
            if(entry.getValue().contains(chunk))
            {
                revealRepeaters(chunk, Bukkit.getPlayer(entry.getKey()));
                plugin.chunkMap.get(entry.getKey()).remove(chunk);
                return;
            }
        }
    }

    @EventHandler
    public void onUnclaimAll(LandUnclaimAllEvent event)
    {
        Chunk chunk;
        for(FLocation loc : event.getFaction().getAllClaims())
        {
            chunk = loc.getChunk();
            for(Map.Entry<UUID, Set<Chunk>> entry : plugin.chunkMap.entrySet())
            {
                if(entry.getValue().contains(chunk))
                {
                    revealRepeaters(chunk, Bukkit.getPlayer(entry.getKey()));
                    plugin.chunkMap.get(entry.getKey()).remove(chunk);
                    break;
                }
            }
        }
    }

    private void revealRepeaters(Chunk chunk, Player player)
    {
        int x1 = chunk.getX() << 4;
        int z1 = chunk.getZ() << 4;
        Block block;
        World world = chunk.getWorld();

        for(int x = x1; x < x1 + 16; x++)
            for(int z = z1; z < z1 + 16; z++)
                for(int y = 0; y < 256; y++)
                {
                    block = world.getBlockAt(x, y, z);
                    if(block.getType().equals(Material.DIODE_BLOCK_OFF))
                    {
//                        AntiSkid.protMan.removePacketListener(plugin.blockChangeListener);
                        new RepeaterRevealPacket(block.getLocation()).broadcastPacket();
//                        AntiSkid.protMan.addPacketListener(plugin.blockChangeListener);

                        plugin.diodeMap.get(player.getUniqueId()).remove(block);
                    }
                }
    }
}
