/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.config.Message;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.utils.BukkitUtil;
import com.reliableplugins.antiskid.utils.ReflectUtil;
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

            // Whitelist Player
            if(args[0].equalsIgnoreCase("add"))
            {
                plugin.startSynchronousTask(()-> whitelistPlayer(player));
            }

            // UnWhitelist Player
            else if(args[0].equalsIgnoreCase("del")
            || args[0].equalsIgnoreCase("delete")
            || args[0].equalsIgnoreCase("rem")
            || args[0].equalsIgnoreCase("remove"))
            {
                plugin.startSynchronousTask(()->unWhitelistPlayer(player));
            }

            // Invalid Arguments
            else
            {
                executor.sendMessage(Message.HELP_WHITELIST.getMessage());
            }
        }

        // No Arguments - print whitelist
        else if(args.length == 0)
        {
            plugin.startSynchronousTask(this::printWhitelist);
        }

        // Invalid Arguments
        else
        {
            executor.sendMessage(Message.HELP_WHITELIST.getMessage());
        }
    }

    private void printWhitelist()
    {
        Whitelist whitelist = plugin.whitelists.get(executorId);

        if(whitelist != null && (whitelist.size() != 0))
        {
            executor.sendMessage(Message.WHITELIST_LIST.getMessage().replace("{LIST}", whitelist.getListString()));
        }
        else
        {
            executor.sendMessage(Message.HELP_WHITELIST.getMessage());
        }
    }

    private void whitelistPlayer(Player player)
    {
        Whitelist whitelist = plugin.whitelists.get(executorId);

        if(player == null)
        {
            executor.sendMessage(Message.ERROR_INVALID_PLAYER.getMessage());
            return;
        }
        if(player.equals(executor))
        {
            executor.sendMessage(Message.ERROR_WHITELIST_SELF.getMessage());
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
            executor.sendMessage(Message.ERROR_PLAYER_ALREADY_WHITELISTED.getMessage().replace("{PLAYER}", player.getName()));
            return;
        }
        executor.sendMessage(Message.WHITELIST_ADD.getMessage().replace("{PLAYER}", player.getName()));

        // If executor has protection, reveal to whitelisted
        if(plugin.diodes.containsKey(executorId))
        {
            Set<Chunk> chunks = plugin.diodes.get(executorId).keySet();
            for(Chunk chunk : chunks)
            {
                BukkitUtil.reloadChunk(chunk);
            }
        }
    }

    private void unWhitelistPlayer(Player player)
    {
        Whitelist whitelist = plugin.whitelists.get(executorId);
        Map<Chunk, Set<Location>> chunkSetMap = plugin.diodes.get(executorId);

        if(player == null)
        {
            executor.sendMessage(Message.ERROR_INVALID_PLAYER.getMessage());
            return;
        }

        if(player.equals(executor))
        {
            executor.sendMessage(Message.ERROR_UNWHITELIST_SELF.getMessage());
            return;
        }


        if(whitelist != null)
        {
            if(!whitelist.removePlayer(player.getUniqueId()))
            {
                executor.sendMessage(Message.ERROR_PLAYER_NOT_WHITELISTED.getMessage().replace("{PLAYER}", player.getName()));
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
            executor.sendMessage(Message.WHITELIST_REM.getMessage().replace("{PLAYER}", player.getName()));
        }
        else
        {
            executor.sendMessage(Message.ERROR_PLAYER_NOT_WHITELISTED.getMessage().replace("{PLAYER}", player.getName()));
        }
    }
}
