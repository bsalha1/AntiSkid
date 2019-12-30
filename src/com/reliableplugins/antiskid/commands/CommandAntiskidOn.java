/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.hook.impl.FactionHook;
import com.reliableplugins.antiskid.packets.RepeaterHidePacket;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CommandBuilder(label = "on", permission = "antiskid.on", description = "Turns on antiskid protection", playerRequired = true)
public class CommandAntiskidOn extends AbstractCommand
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
        Set<Chunk> chunks = FactionHook.findChunkGroup(executor, executor.getLocation().getChunk());

        // If the chunks are not the executors...
        if(chunks.isEmpty())
        {
            executor.sendMessage(Message.ERROR_NOT_TERRITORY.toString());
            return;
        }

        World world = chunks.iterator().next().getWorld();
        Set<Location> diodeLocations = new HashSet<>();
        Map<Chunk, Set<Location>> diodes = new HashMap<>();

        for(Chunk c : chunks)
        {
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
            diodeLocations.clear();
        }

        // This will overwrite the chunk keys with new locations
        plugin.diodes.get(executorId).putAll(diodes);

        // If whitelist hasn't already been populated, populate it with the executor
        if(!plugin.whitelists.containsKey(executorId))
        {
            plugin.whitelists.put(executorId, new TreeSet<>());
            plugin.whitelists.get(executorId).add(executorId);
        }

        TreeSet<UUID> whitelist = plugin.whitelists.get(executorId);

        // Change diodes to carpets for all players not in whitelist
        for(Map.Entry<Chunk, Set<Location>> entry : diodes.entrySet())
        {
            for(Location loc : entry.getValue())
            {
                new RepeaterHidePacket(loc).broadcastPacket(whitelist);
            }
        }

        executor.sendMessage(Message.ANTISKID_ON.toString().replace("{NUM}", Integer.toString(count)));
    }

}
