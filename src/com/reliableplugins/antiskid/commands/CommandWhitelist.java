/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@CommandBuilder(label = "whitelist", permission = "antiskid.whitelist", playerRequired = true, description = "Manage the whitelist for the executor's protection.\nIf they are added, they can see the repeaters.")
public class CommandWhitelist extends AbstractCommand
{
    private Player executor;
    private UUID executorId;

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        this.executor = (Player) sender;
        this.executorId = executor.getUniqueId();

        // If /antiskid whitelist <add/del> <player>
        if(args.length == 2)
        {
            Player player = Bukkit.getPlayer(args[1]);
            if(args[0].equalsIgnoreCase("add")) // If adding player
            {
                whitelistPlayer(player);
            }
            else if(args[0].equalsIgnoreCase("del")
            || args[0].equalsIgnoreCase("delete")
            || args[0].equalsIgnoreCase("rem")
            || args[0].equalsIgnoreCase("remove")) // If deleting player
            {
                unWhitelistPlayer(player);
            }
            else // If invalid whitelist argument
            {
                executor.sendMessage(Message.HELP_WHITELIST.toString());
            }
        }
        // If /antiskid whitelist
        else if(args.length == 0)
        {
            printWhitelist();
        }
        // If /antiskid whitelist <invalid arguments>
        else
        {
            executor.sendMessage(Message.HELP_WHITELIST.toString());
        }
    }


    /**
     * Prints executor's whitelist
     */
    private void printWhitelist()
    {
        TreeSet<UUID> whitelist = plugin.whitelists.get(executorId);
        Player player;

        if(whitelist == null)
        {
            executor.sendMessage(Message.HELP_WHITELIST.toString());
        }
        else
        {
            StringBuilder whitelistMsg = new StringBuilder();
            for(UUID id : whitelist)
            {
                player = Bukkit.getPlayer(id);
                if(player.equals(executor)) continue;
                whitelistMsg.append(player.getName()).append(", ");
            }

            // Trim off trailing comma
            if(whitelistMsg.toString().contains(", "))
            {
                whitelistMsg = new StringBuilder(whitelistMsg.substring(0, whitelistMsg.lastIndexOf(", ")));
                executor.sendMessage(Message.WHITELIST_LIST.toString().replace("{LIST}", whitelistMsg));
                return;
            }
            executor.sendMessage(Message.ERROR_EMPTY_WHITELIST.toString());
        }
    }


    /**
     * Adds a player to executor's whitelist
     * @param player player to add
     */
    private void whitelistPlayer(Player player)
    {
        TreeSet<UUID> whitelist = plugin.whitelists.get(executorId);

        if(player == null)
        {
            executor.sendMessage(Message.ERROR_INVALID_PLAYER.toString());
            return;
        }
        if(player.equals(executor))
        {
            executor.sendMessage(Message.ERROR_WHITELIST_SELF.toString());
            return;
        }

        // If the whitelist isn't initialized, initialize it
        if(whitelist == null)
        {
            plugin.whitelists.put(executorId, new TreeSet<>());
            plugin.whitelists.get(executorId).add(executor.getUniqueId());
            plugin.whitelists.get(executorId).add(player.getUniqueId());
        }
        else if(whitelist.contains(player.getUniqueId()))
        {
            executor.sendMessage(Message.ERROR_PLAYER_ALREADY_WHITELISTED.toString().replace("{PLAYER}", player.getName()));
            return;
        }
        else
        {
            whitelist.add(player.getUniqueId());
        }
        executor.sendMessage(Message.WHITELIST_ADD.toString().replace("{PLAYER}", player.getName()));

        if(!plugin.diodes.containsKey(executorId))
        {
            return;
        }

        // Reveal hidden repeaters
        Set<Chunk> chunks = plugin.diodes.get(executorId).keySet();
        for(Chunk chunk : chunks)
        {
            Util.reloadChunk(chunk);
        }
    }


    /**
     * Remove player from executor's whitelist
     * @param player player to remove
     */
    private void unWhitelistPlayer(Player player)
    {
        TreeSet<UUID> whitelist = plugin.whitelists.get(executorId);
        Collection<Set<Location>> diodes = plugin.diodes.get(executorId).values();

        // If invalid player... throw error
        if(player == null)
        {
            executor.sendMessage(Message.ERROR_INVALID_PLAYER.toString());
            return;
        }

        // If self... throw error
        if(player.equals(executor))
        {
            executor.sendMessage(Message.ERROR_UNWHITELIST_SELF.toString());
            return;
        }

        // If executor has no whitelist... throw error
        if(whitelist == null)
        {
            executor.sendMessage(Message.ERROR_PLAYER_NOT_WHITELISTED.toString().replace("{PLAYER}", player.getName()));
        }
        // If whitelist contains player... remove them
        else if(whitelist.contains(player.getUniqueId()))
        {
            whitelist.remove(player.getUniqueId());

            // If executor has protected diodes, hide them from the removed player
            for(Set<Location> locs : diodes)
            {
                for(Location loc : locs)
                {
                    plugin.getNMS().sendBlockChangePacket(player, Material.CARPET, loc);
                }
            }

            executor.sendMessage(Message.WHITELIST_REM.toString().replace("{PLAYER}", player.getName()));
        }
        // If whitelist doesn't contain player... throw error
        else
        {
            executor.sendMessage(Message.ERROR_PLAYER_NOT_WHITELISTED.toString().replace("{PLAYER}", player.getName()));
        }
    }
}
