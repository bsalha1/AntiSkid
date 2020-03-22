/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@CommandBuilder(label = "whitelist", permission = "antiskid.whitelist", playerRequired = true, description = "Manage the whitelist for the executor's protection.\nIf they are added, they can see the repeaters.")
public class CommandWhitelist extends Command
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
                plugin.startSyncTask(()-> whitelistPlayer(player));
            }
            else if(args[0].equalsIgnoreCase("del")
            || args[0].equalsIgnoreCase("delete")
            || args[0].equalsIgnoreCase("rem")
            || args[0].equalsIgnoreCase("remove")) // If deleting player
            {
                plugin.startSyncTask(()->unWhitelistPlayer(player));
            }
            else // If invalid whitelist argument
            {
                executor.sendMessage(plugin.getMessageManager().HELP_WHITELIST);
            }
        }
        // If /antiskid whitelist
        else if(args.length == 0)
        {
            plugin.startSyncTask(this::printWhitelist);
        }
        // If /antiskid whitelist <invalid arguments>
        else
        {
            executor.sendMessage(plugin.getMessageManager().HELP_WHITELIST);
        }
    }


    /**
     * Prints executor's whitelist
     */
    private void printWhitelist()
    {
        Whitelist whitelist = plugin.whitelists.get(executorId);

        if(whitelist != null && (whitelist.size() != 0))
        {
            executor.sendMessage(plugin.getMessageManager().WHITELIST_LIST.replace("{LIST}", whitelist.getListString()));
        }
        else
        {
            executor.sendMessage(plugin.getMessageManager().HELP_WHITELIST);
        }
    }


    /**
     * Adds a player to executor's whitelist
     * @param player player to add
     */
    private void whitelistPlayer(Player player)
    {
        Whitelist whitelist = plugin.whitelists.get(executorId);

        if(player == null)
        {
            executor.sendMessage(plugin.getMessageManager().ERROR_INVALID_PLAYER);
            return;
        }
        if(player.equals(executor))
        {
            executor.sendMessage(plugin.getMessageManager().ERROR_WHITELIST_SELF);
            return;
        }

        // If the whitelist isn't initialized, initialize it
        if(whitelist == null)
        {
            plugin.whitelists.put(executorId, new Whitelist(executorId));
            plugin.whitelists.get(executorId).addPlayer(player.getUniqueId());
        }
        else if(!whitelist.addPlayer(player.getUniqueId()))
        {
            executor.sendMessage(plugin.getMessageManager().ERROR_PLAYER_ALREADY_WHITELISTED.replace("{PLAYER}", player.getName()));
            return;
        }
        executor.sendMessage(plugin.getMessageManager().WHITELIST_ADD.replace("{PLAYER}", player.getName()));

        // If executor has protection, reveal to whitelisted
        if(plugin.diodes.containsKey(executorId))
        {
            Set<Chunk> chunks = plugin.diodes.get(executorId).keySet();
            for(Chunk chunk : chunks)
            {
                Util.reloadChunk(chunk);
            }
        }
    }


    /**
     * Remove player from executor's whitelist
     * @param player player to remove
     */
    private void unWhitelistPlayer(Player player)
    {
        Whitelist whitelist = plugin.whitelists.get(executorId);
        Map<Chunk, Set<Location>> chunkSetMap = plugin.diodes.get(executorId);

        if(player == null)
        {
            executor.sendMessage(plugin.getMessageManager().ERROR_INVALID_PLAYER);
            return;
        }

        if(player.equals(executor))
        {
            executor.sendMessage(plugin.getMessageManager().ERROR_UNWHITELIST_SELF);
            return;
        }


        if(whitelist != null)
        {
            if(!whitelist.removePlayer(player.getUniqueId()))
            {
                executor.sendMessage(plugin.getMessageManager().ERROR_PLAYER_NOT_WHITELISTED.replace("{PLAYER}", player.getName()));
                return;
            }

            if(chunkSetMap != null)
            {
                Collection<Set<Location>> diodeLocations = chunkSetMap.values();
                for(Set<Location> locations : diodeLocations)
                {
                    for(Location location : locations)
                    {
                        plugin.getNMS().sendBlockChangePacket(player, plugin.getReplacer(), location);
                    }
                }
            }
            executor.sendMessage(plugin.getMessageManager().WHITELIST_REM.replace("{PLAYER}", player.getName()));
        }
        else
        {
            executor.sendMessage(plugin.getMessageManager().ERROR_PLAYER_NOT_WHITELISTED.replace("{PLAYER}", player.getName()));
        }
    }
}
