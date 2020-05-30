/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.config.Message;
import com.reliableplugins.antiskid.hook.FactionHook;
import com.reliableplugins.antiskid.type.SelectionTool;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.type.Pair;
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
        AntiSkid plugin = AntiSkid.INSTANCE;

        //
        // FACTIONS
        //
        if(plugin.getMainConfig().factionsWorlds.contains(executor.getWorld())) // If this is a factions world...
        {
            if(FactionHook.getRole(executor) < plugin.getMainConfig().minimumFactionRank)
            {
                executor.sendMessage(Message.ERROR_LOW_RANK.getMessage().replace("{RANK}", FactionHook.getRoleName(plugin.getMainConfig().minimumFactionRank)));
                return;
            }

            chunks = FactionHook.findChunkGroup(executor, executor.getLocation().getChunk());
            if(chunks.isEmpty())
            {
                executor.sendMessage(Message.ERROR_NOT_TERRITORY.getMessage());
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

            plugin.startSynchronousTask(()->
            {
                if(plugin.getMainConfig().whitelistFaction)
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
        // PLOT SQUARED
        //
        else if(plugin.getMainConfig().plotsWorlds.contains(executor.getWorld())) // If this is a plots world...
        {
            if(!plugin.getMainConfig().plotSquaredHook.isOwner(executor, executor.getLocation()))
            {
                executor.sendMessage(Message.ERROR_NOT_PLOT_OWNER.getMessage());
                return;
            }

            chunks = plugin.getMainConfig().plotSquaredHook.getChunks(executor.getLocation());
            if(chunks.isEmpty())
            {
                executor.sendMessage(Message.ERROR_NOT_PLOT_OWNER.getMessage());
                return;
            }

            // If no whitelist
            if(!plugin.whitelists.containsKey(executorId))
            {
                plugin.startSynchronousTask(()-> plugin.whitelists.put(executorId, new Whitelist(executorId)));
            }
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
                    executor.sendMessage(Message.ERROR_NO_POSITION1.getMessage());
                    return;
                }
                else if(locations.getValue() == null) // If no pos2
                {
                    executor.sendMessage(Message.ERROR_NO_POSITION2.getMessage());
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
                plugin.startSynchronousTask(()-> plugin.whitelists.put(executorId, new Whitelist(executorId)));
            }
        }
        else
        {
            executor.sendMessage(Message.ERROR_INVALID_WORLD.getMessage());
        }

        for(Chunk chunk : chunks)
        {
            if(plugin.cache.isProtected(chunk))
            {
                executor.sendMessage(Message.ERROR_ALREADY_PROTECTED.getMessage());
                return;
            }
        }

        /* Cache Diodes */
        if(plugin.getMainConfig().fastScan)
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
            plugin.startSynchronousTask(()->plugin.diodes.put(executorId, new HashMap<>()));
        }

        // Register diodes
        plugin.startSynchronousTask(()->plugin.diodes.get(executorId).putAll(diodes));

        for(Set<Location> locations : diodes.values())
        {
            protectDiodes(locations);
        }

        executor.sendMessage(Message.ANTISKID_ON.getMessage().replace("{NUM}", Integer.toString(diodes.keySet().size())));
    }

    private void protectDiodes(Set<Location> locations)
    {
        for(Location location : locations)
        {
            try
            {

                AntiSkid.INSTANCE.getNMS().broadcastBlockChangePacket(
                        AntiSkid.INSTANCE.getReplacer(),
                        location,
                        AntiSkid.INSTANCE.whitelists.get(executorId).getUUIDs());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
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
