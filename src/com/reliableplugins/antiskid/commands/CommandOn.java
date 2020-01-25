/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.hook.impl.FactionHook;
import com.reliableplugins.antiskid.type.Whitelist;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Executors;

@CommandBuilder(label = "on", permission = "antiskid.on", description = "Turns on protection for the chunk group the executor is in.\nAnyone besides the executor and the people on their whitelist\ncan see the repeaters in this chunk group.", playerRequired = true)
public class CommandOn extends AbstractCommand
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
        Set<Chunk> chunks = FactionHook.findChunkGroup(executor, executor.getLocation().getChunk());
        if(chunks.isEmpty())
        {
            executor.sendMessage(plugin.getMessageManager().ERROR_NOT_TERRITORY);
            return;
        }

        try
        {
            plugin.lock.acquire();
        } catch(Exception ignored) { }

        if(!plugin.diodes.containsKey(executorId))
        {
            plugin.diodes.put(executorId, new HashMap<>());
        }
        if(!plugin.whitelists.containsKey(executorId))
        {
            plugin.whitelists.put(executorId, new Whitelist(executorId));
        }
        Whitelist whitelist = plugin.whitelists.get(executorId);

        /* Cache Diodes */
        Map<Chunk, Set<Location>> diodes;
        if(plugin.getMainConfig().getFileConfiguration().getBoolean("fast-scan"))
        {
            diodes = fastScan(chunks, whitelist);
        }
        else
        {
            diodes = regularScan(chunks, whitelist);
        }
        plugin.diodes.get(executorId).putAll(diodes);

        plugin.lock.release();

        executor.sendMessage(plugin.getMessageManager().ANTISKID_ON.replace("{NUM}", Integer.toString(diodes.keySet().size())));
    }

    /**
     * Scan chunks with dispensers in them for diodes
     * @param chunks to scan
     * @param whitelist players who shouldn't receive blockchange
     * @return map of chunk to locations of diodes
     */
    private Map<Chunk, Set<Location>> fastScan(Set<Chunk> chunks, Whitelist whitelist)
    {
        Map<Chunk, Set<Location>> diodes = new HashMap<>();
        for(Chunk chunk : chunks)
        {
            for(BlockState state : chunk.getTileEntities())
            {
                if(state.getType().equals(Material.DISPENSER))
                {
                    diodes.put(chunk, findAndProtectDiodes(chunk, whitelist));
                    break;
                }
            }
        }
        return diodes;
    }

    /**
     * Scan all chunks for diodes
     * @param chunks to scan
     * @param whitelist players who shouldn't receive blockchange
     * @return map of chunk to locations of diodes
     */
    private Map<Chunk, Set<Location>> regularScan(Set<Chunk> chunks, Whitelist whitelist)
    {
        Map<Chunk, Set<Location>> diodes = new HashMap<>();
        for(Chunk chunk : chunks)
        {
            diodes.put(chunk, findAndProtectDiodes(chunk, whitelist));
        }
        return diodes;
    }

    private HashSet<Location> findAndProtectDiodes(Chunk chunk, Whitelist whitelist)
    {
        HashSet<Location> diodeLocations = new HashSet<>();
        int x1 = chunk.getX() << 4;
        int z1 = chunk.getZ() << 4;
        World world = chunk.getWorld();
        Block block;
        Location location;

        for(int x = x1; x < x1 + 16; x++)
            for(int z = z1; z < z1 + 16; z++)
                for(int y = 0; y < 256; y++)
                {
                    block = world.getBlockAt(x, y, z);
                    if(block.getType().equals(Material.DIODE_BLOCK_OFF))
                    {
                        location = block.getLocation();
                        diodeLocations.add(location);
                        plugin.getNMS().broadcastBlockChangePacket(Material.CARPET, location, whitelist.getUUIDs());
                    }
                }
        return diodeLocations;
    }
}
