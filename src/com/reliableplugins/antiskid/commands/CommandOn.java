/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.hook.FactionHook;
import com.reliableplugins.antiskid.hook.PlotSquaredHook;
import com.reliableplugins.antiskid.type.Whitelist;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Executors;

@CommandBuilder(label = "on", permission = "antiskid.on", description = "Turns on protection for the chunk group the executor is in.\nAnyone besides the executor and the people on their whitelist\ncan see the repeaters in this chunk group.", playerRequired = true)
public class CommandOn extends Command
{
    private Player executor;
    private UUID executorId;

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        executor = (Player) sender;
        executorId = executor.getUniqueId();

        Executors.newSingleThreadExecutor().submit(this::antiskidOn);
    }

    private void antiskidOn()
    {
        Set<Chunk> chunks = new HashSet<>();
        Map<Chunk, Set<Location>> diodes;

        try{ plugin.lock.acquire(); } catch(Exception ignored){}

        //
        // FACTIONS
        //
        if(plugin.getFactionsWorlds().contains(executor.getWorld())) // If this is a factions world...
        {
            chunks = FactionHook.findChunkGroup(executor, executor.getLocation().getChunk());
            if(chunks.isEmpty())
            {
                executor.sendMessage(plugin.getMessageManager().ERROR_NOT_TERRITORY);
                plugin.lock.release();
                return;
            }
        }

        //
        // PLOT SQUARED
        //
        else if(plugin.getPlotsWorlds().contains(executor.getWorld())) // If this is a plots world...
        {
            if(!PlotSquaredHook.isOwner(executor, executor.getLocation()))
            {
                executor.sendMessage(plugin.getMessageManager().ERROR_NOT_PLOT_OWNER);
                plugin.lock.release();
                return;
            }

            /* Cache Diodes */
            Bukkit.broadcastMessage("chunks");
            chunks = PlotSquaredHook.getChunks(executor.getLocation());
            Bukkit.broadcastMessage(chunks.toString());
        }

        /* Cache Diodes */
        if(plugin.getMainConfig().getFileConfiguration().getBoolean("fast-scan"))
        {
            diodes = fastScan(chunks);
        }
        else
        {
            diodes = regularScan(chunks);
        }

        // If first time
        if(!plugin.diodes.containsKey(executorId))
        {
            plugin.diodes.put(executorId, new HashMap<>());
        }

        // If no whitelist
        if(!plugin.whitelists.containsKey(executorId))
        {
            plugin.whitelists.put(executorId, new Whitelist(executorId));
        }

        // Register diodes
        plugin.diodes.get(executorId).putAll(diodes);
        plugin.lock.release();

        for(Set<Location> locations : diodes.values())
        {
            protectDiodes(locations);
        }
        executor.sendMessage(plugin.getMessageManager().ANTISKID_ON.replace("{NUM}", Integer.toString(diodes.keySet().size())));
    }

    private void protectDiodes(Set<Location> locations)
    {
        for(Location location : locations)
        {
            plugin.getNMS().broadcastBlockChangePacket(Material.CARPET, location, plugin.whitelists.get(executorId).getUUIDs());
        }
    }

    public static Map<Chunk, Set<Location>> fastScan(Set<Chunk> chunks)
    {
        Map<Chunk, Set<Location>> diodes = new HashMap<>();
        for(Chunk chunk : chunks)
        {
            for(BlockState state : chunk.getTileEntities())
            {
                if(state.getType().equals(Material.DISPENSER))
                {
                    diodes.put(chunk, getDiodes(chunk));
                    break;
                }
            }
        }
        return diodes;
    }

    public static Map<Chunk, Set<Location>> regularScan(Set<Chunk> chunks)
    {
        Map<Chunk, Set<Location>> diodes = new HashMap<>();
        for(Chunk chunk : chunks)
        {
            diodes.put(chunk, getDiodes(chunk));
        }
        return diodes;
    }

    private static HashSet<Location> getDiodes(Chunk chunk)
    {
        HashSet<Location> diodeLocations = new HashSet<>();
        int x1 = chunk.getX() << 4;
        int z1 = chunk.getZ() << 4;
        World world = chunk.getWorld();
        Block block;

        for(int x = x1; x < x1 + 16; x++)
            for(int z = z1; z < z1 + 16; z++)
                for(int y = 0; y < 256; y++)
                {
                    block = world.getBlockAt(x, y, z);
                    if(block.getType().equals(Material.DIODE_BLOCK_OFF))
                    {
                        diodeLocations.add(block.getLocation());
                    }
                }

        return diodeLocations;
    }


}
