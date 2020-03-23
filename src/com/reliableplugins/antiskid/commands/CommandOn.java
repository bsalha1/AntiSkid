/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.hook.FactionHook;
import com.reliableplugins.antiskid.type.SelectionTool;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.type.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Executors;

//import com.reliableplugins.antiskid.hook.PlotSquaredHook;

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
        Set<Chunk> chunks = new HashSet<Chunk>();
        Map<Chunk, Set<Location>> diodes;

        //
        // FACTIONS
        //
        if(plugin.config.factionsWorlds.contains(executor.getWorld())) // If this is a factions world...
        {
            if(FactionHook.getRole(executor) < plugin.config.minimumFactionRank)
            {
                executor.sendMessage(plugin.getMessageManager().ERROR_LOW_RANK.replace("{RANK}", FactionHook.getRoleName(plugin.config.minimumFactionRank)));
                return;
            }

            chunks = FactionHook.findChunkGroup(executor, executor.getLocation().getChunk());
            if(chunks.isEmpty())
            {
                executor.sendMessage(plugin.getMessageManager().ERROR_NOT_TERRITORY);
                return;
            }

            Whitelist whitelist;
            if(!plugin.whitelists.containsKey(executorId)) // if no whitelist
            {
                whitelist = new Whitelist(executorId);
            }
            else
            {
                whitelist = plugin.whitelists.get(executorId);
            }

            plugin.startSyncTask(()->
            {
                if(plugin.config.isFactionWhitelisted)
                {
                    for(Player player : FactionHook.getFactionMembers(executor))
                    {
                        whitelist.addPlayer(player);
                    }
                }
                plugin.whitelists.put(executorId, whitelist);
            });
        }

        //
        // SELECTION TOOL
        //
        else if(executor.getInventory().contains(SelectionTool.getItem()))
        {
            Pair<Location, Location> locations = plugin.selectionPoints.get(executorId);
            if(locations != null) // If selection set
            {
                if(locations.getKey() == null) // If no pos1
                {
                    executor.sendMessage(plugin.getMessageManager().ERROR_NO_POSITION1);
                    return;
                }
                else if(locations.getValue() == null) // If no pos2
                {
                    executor.sendMessage(plugin.getMessageManager().ERROR_NO_POSITION2);
                    return;
                }
                else // Valid pos1 and pos2
                {
                    chunks = getChunksFromSelection(locations.getKey(), locations.getValue());
                }
            }

            // If no whitelist
            if(!plugin.whitelists.containsKey(executorId))
            {
                plugin.startSyncTask(()-> plugin.whitelists.put(executorId, new Whitelist(executorId)));
            }
        }

        //
        // PLOT SQUARED
        //
//        else if(plugin.getPlotsWorlds().contains(executor.getWorld())) // If this is a plots world...
//        {
//            if(!PlotSquaredHook.isOwner(executor, executor.getLocation()))
//            {
//                executor.sendMessage(plugin.getMessageManager().ERROR_NOT_PLOT_OWNER);
//                plugin.lock.release();
//                return;
//            }
//
//            /* Cache Diodes */
//            chunks = PlotSquaredHook.getChunks(executor.getLocation());
//        }

        for(Chunk chunk : chunks)
        {
            if(plugin.cache.isProtected(chunk))
            {
                executor.sendMessage(plugin.getMessageManager().ERROR_ALREADY_PROTECTED);
                return;
            }
        }

        /* Cache Diodes */
        if(plugin.config.isFastScan)
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
            plugin.startSyncTask(()->plugin.diodes.put(executorId, new HashMap<>()));
        }

        // Register diodes
        plugin.startSyncTask(()->plugin.diodes.get(executorId).putAll(diodes));

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
            plugin.getNMS().broadcastBlockChangePacket(plugin.getReplacer(), location, plugin.whitelists.get(executorId).getUUIDs());
        }
    }

    public static Map<Chunk, Set<Location>> fastScan(Set<Chunk> chunks)
    {
        Map<Chunk, Set<Location>> diodes = new HashMap<>();
        for(Chunk chunk : chunks)
        {
            BlockState[] states = chunk.getTileEntities();
            if(states == null || states.length == 0)
            {
                continue;
            }

            for(BlockState state : states)
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

    private static HashSet<Chunk> getChunksFromSelection(Location pos1, Location pos2)
    {
        World world = pos1.getWorld();
        HashSet<Chunk> chunks = new HashSet<>();
        int minX = Math.min((int) pos1.getX(), (int) pos2.getX());
        int maxX = Math.max((int) pos1.getX(), (int) pos2.getX());

        int minZ = Math.min((int) pos1.getZ(), (int) pos2.getZ());
        int maxZ = Math.max((int) pos1.getZ(), (int) pos2.getZ());

        for(int x = minX; x < maxX; x++)
            for(int z = minZ; z < maxZ; z++)
            {
                chunks.add(world.getChunkAt(x >> 4, z >> 4));
            }
        return chunks;
    }
}
