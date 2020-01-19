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
        int count = 0;
        int x1;
        int z1;
        Block block;

        try
        {
            plugin.lock.acquire();
        }
        catch(Exception ignored) { }

        if(!plugin.diodes.containsKey(executorId))
        {
            plugin.diodes.put(executorId, new HashMap<>());
        }

        Set<Chunk> chunks = FactionHook.findChunkGroup(executor, executor.getLocation().getChunk());
        if(chunks.isEmpty())
        {
            plugin.lock.release();
            executor.sendMessage(plugin.getMessageManager().ERROR_NOT_TERRITORY);
            return;
        }

        World world = chunks.iterator().next().getWorld();
        Set<Location> diodeLocations;
        Map<Chunk, Set<Location>> diodes = new HashMap<>();

        // For each claimed chunk, put the diode locations associated with that chunk
        for(Chunk c : chunks)
        {
            diodeLocations = new HashSet<>();
            x1 = c.getX() << 4;
            z1 = c.getZ() << 4;
            for(int x = x1; x < x1 + 16; x++)
                for(int z = z1; z < z1 + 16; z++)
                    for(int y = 0; y < 256; y++)
                    {
                        block = world.getBlockAt(x, y, z);
                        if(block.getType().equals(Material.DIODE_BLOCK_OFF))
                        {
                            diodeLocations.add(block.getLocation());
                            count++;
                        }
                    }
            diodes.put(c, diodeLocations);
        }

        // This will overwrite the chunk keys with new locations
        plugin.diodes.get(executorId).putAll(diodes);

        // If whitelist hasn't already been populated, populate it with the executor
        if(!plugin.whitelists.containsKey(executorId))
        {
            plugin.whitelists.put(executorId, new Whitelist(executorId));
        }

        Whitelist whitelist = plugin.whitelists.get(executorId);

        // Change diodes to carpets for all players not in whitelist
        for(Set<Location> locs : diodes.values())
            for(Location loc : locs)
            {
                plugin.getNMS().broadcastBlockChangePacket(Material.CARPET, loc, whitelist.getUUIDs());
            }
        plugin.lock.release();

        executor.sendMessage(plugin.getMessageManager().ANTISKID_ON.replace("{NUM}", Integer.toString(count)));
    }
}