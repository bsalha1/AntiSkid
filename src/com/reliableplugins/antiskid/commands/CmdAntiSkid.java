/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.hook.impl.FactionHook;
import com.reliableplugins.antiskid.packets.RepeaterHidePacket;
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

public class CmdAntiSkid implements CommandExecutor
{
    private AntiSkid antiSkid;
    private Player executor;
    private Set<Player> whitelist;
    private Set<Block> diodes;

    public CmdAntiSkid(AntiSkid antiSkid)
    {
        this.antiSkid = antiSkid;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if(!(commandSender instanceof Player))
        {
            commandSender.sendMessage(Message.ERROR_NOT_PLAYER.toString());
            return true;
        }

        this.executor = (Player) commandSender;
        this.whitelist = antiSkid.whitelists.get(executor);
        this.diodes = antiSkid.diodeMap.get(executor);

        /* NOT ENOUGH ARGUMENTS */
        if(strings.length < 1)
        {
            executor.sendMessage(Message.HELP_ANTISKID.toString());
            return true;
        }

        /* ANTISKID ON */
        else if(strings[0].equalsIgnoreCase("on"))
        {
            Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().build()).submit(this::antiskidOn); // Run asynchronously
            return true;
        }


        /* ANTISKID OFF */
        else if(strings[0].equalsIgnoreCase("off"))
        {
            Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().build()).submit(this::antiskidOff); // Run asynchronously
            return true;
        }


        /* WHITELIST */
        else if(strings[0].equalsIgnoreCase("whitelist"))
        {
            // If /antiskid whitelist <no arguments>
            if(strings.length == 1)
            {
                printWhitelist();
                return true;
            }

            // If /antiskid whitelist <arg1> <arg2>
            else if(strings.length == 3)
            {
                Player target = Bukkit.getPlayer(strings[2]);

                // If /antiskid whitelist <add> <arg2>
                if(strings[1].equalsIgnoreCase("add"))
                {
                    whitelistPlayer(target);
                    return true;
                }

                // If /antiskid whitelist <del/delete/rem/remove> <arg2>
                else if(
                        strings[1].equalsIgnoreCase("del")
                        || strings[1].equalsIgnoreCase("delete")
                        || strings[1].equalsIgnoreCase("rem")
                        || strings[1].equalsIgnoreCase("remove"))
                {
                    unWhitelistPlayer(target);
                    return true;
                }

                // If /antiskid whitelist <invalid argument>
                else
                {
                    executor.sendMessage(Message.HELP_WHITELIST.toString());
                    return true;
                }
            }

            // If /antiskid whitelist <invalid arguments>
            else
            {
                executor.sendMessage(Message.HELP_WHITELIST.toString());
                return true;
            }
        }


        /* BAD ARGUMENT */
        else
        {
            executor.sendMessage(Message.HELP_ANTISKID.toString());
        }



        return true;
    }


    /**
     * Prints the executor's whitelist
     */
    private void printWhitelist()
    {
        // If whitelist isn't initialized, throw error
        if(whitelist == null)
        {
            executor.sendMessage(Message.ERROR_NO_WHITELIST.toString());
        }
        // Else print the whitelist (not including the executor)
        else
        {
            String message = "";
            for(Player p : whitelist)
            {
                if(p.equals(executor)) continue;
                message = message + p.getName() + ", ";
            }

            // Trim off trailing comma
            if(message.contains(", "))
            {
                message = message.substring(0, message.lastIndexOf(", "));
                executor.sendMessage(String.format(Message.LIST_WHITELISTED.toString(), message));
                return;
            }
            executor.sendMessage(Message.ERROR_NO_WHITELIST.toString());
        }
    }


    /**
     * Removes player from executors whitelist
     * @param player player to remove from whitelist
     */
    private void unWhitelistPlayer(Player player)
    {
        // If invalid player... throw error
        if(player == null)
        {
            executor.sendMessage(Message.ERROR_INVALID_PLAYER.toString());
            return;
        }

        // If executor has no whitelist... throw error
        if(whitelist == null)
        {
            executor.sendMessage(Message.ERROR_NO_WHITELIST.toString());
            return;
        }
        // If whitelist contains player... remove them
        else if(whitelist.contains(player))
        {
            whitelist.remove(player);

            // If executor has protected diodes, hide them from the removed player
            if(diodes != null)
            {
                for(Block b : diodes) new RepeaterHidePacket(b).sendPacket(player); // Hide repeaters
            }

            executor.sendMessage(String.format(Message.UNWHITELISTED.toString(), player.getName()));
            return;
        }
        // If whitelist doesn't contain player... throw error
        else
        {
            executor.sendMessage(String.format(Message.ERROR_PLAYER_NOT_WHITELISTED.toString(), player.getName()));
            return;
        }
    }


    /**
     * Adds player to executor's whitelist
     * @param player player to add to whitelist
     */
    private void whitelistPlayer(Player player)
    {
        // If invalid player... throw error
        if(player == null)
        {
            executor.sendMessage(Message.ERROR_INVALID_PLAYER.toString());
            return;
        }

        // If the whitelist isn't initialized, initialize it
        if(whitelist == null)
        {
            antiSkid.whitelists.put(executor, new HashSet<>(Arrays.asList(executor, player)));
        }
        // If the whitelist already contains the player, throw error
        else if(whitelist.contains(player))
        {
            executor.sendMessage(String.format(Message.ERROR_PLAYER_ALREADY_WHITELISTED.toString(), player.getName()));
            return;
        }
        // Else, append player onto whitelist
        else
        {
            whitelist.add(player);
        }

        // Reveal the hidden repeaters
        AntiSkid.protMan.removePacketListener(antiSkid.blockChangeListener);
        if(diodes != null)
        {
            for(Block b : diodes) new RepeaterRevealPacket(b).sendPacket(player); // Reveal repeaters
        }
        AntiSkid.protMan.addPacketListener(antiSkid.blockChangeListener);

        executor.sendMessage(String.format(Message.WHITELISTED.toString(), player.getName()));
    }

    /**
     * Turns antiskid protection on. Parse region for repeaters and broadcast a packet change for all blacklisted players
     */
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

        // If player already has protected chunks
        if(antiSkid.chunkMap.containsKey(executor))
        {
            antiSkid.chunkMap.get(executor).addAll(chunks);
        }
        // If player doesn't have protected chunks
        else
        {
            antiSkid.chunkMap.put(executor, chunks);
        }

        World world = chunks.iterator().next().getWorld();
        Set<Block> diodes = new HashSet<>();

        long start = System.currentTimeMillis();
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
                            diodes.add(block);
                            count++;
                        }
                    }
        }
        long end = System.currentTimeMillis();
        Bukkit.broadcastMessage("Took " + Long.toString(end - start) + " ms");

        // If whitelist hasn't already been populated, populate it with the executor
        if(!antiSkid.whitelists.containsKey(executor))
        {
            antiSkid.whitelists.put(executor, new HashSet<>(Collections.singletonList(executor)));
        }

        // Change diodes to carpets for all players not in whitelist
        for(Block b : diodes)
        {
            new RepeaterHidePacket(b).broadcastPacket(antiSkid.whitelists.get(executor));
        }

        // Store diodes
        antiSkid.diodeMap.put(executor, diodes);

        executor.sendMessage(String.format(Message.ANTISKID_ON.toString(), count));
    }

    private int sign(int number)
    {
        return (number == 0)? 1 : number / Math.abs(number);
    }


    /**
     * Turns antiskid protection off
     */
    private void antiskidOff()
    {
        if(diodes == null) // If there are no diodes registered
        {
            executor.sendMessage(Message.ERROR_NO_PROTECTED.toString());
            return;
        }

        AntiSkid.protMan.removePacketListener(antiSkid.blockChangeListener);
        for(Block b : diodes) new RepeaterRevealPacket(b).broadcastPacket(whitelist); // Revert the diode for all blacklisted players
        AntiSkid.protMan.addPacketListener(antiSkid.blockChangeListener);

        antiSkid.diodeMap.remove(executor);
        executor.sendMessage(Message.ANTISKID_OFF.toString());
    }
}
