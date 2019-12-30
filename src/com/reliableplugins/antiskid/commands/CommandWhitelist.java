/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.packets.RepeaterHidePacket;
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@CommandBuilder(label = "whitelist", permission = "antiskid.whitelist")
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

        // If whitelist isn't initialized, throw error
        if(whitelist == null)
        {
            executor.sendMessage(Message.ERROR_EMPTY_WHITELIST.toString());
        }
        // Else print the whitelist (not including the executor)
        else
        {
            StringBuilder message = new StringBuilder();
            for(UUID id : whitelist)
            {
                player = Bukkit.getPlayer(id);
                if(player.equals(executor)) continue;
                message.append(player.getName()).append(", ");
            }

            // Trim off trailing comma
            if(message.toString().contains(", "))
            {
                message = new StringBuilder(message.substring(0, message.lastIndexOf(", ")));
                executor.sendMessage(Message.WHITELIST_LIST.toString().replace("{WHITELIST}", message));
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
        Set<Location> diodes = plugin.diodeMap.get(executorId);

        // If invalid player... throw error
        if(player == null)
        {
            executor.sendMessage(Message.ERROR_INVALID_PLAYER.toString());
            return;
        }

        // If self... throw error
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
        // If the whitelist already contains the player, throw error
        else if(whitelist.contains(player.getUniqueId()))
        {
            executor.sendMessage(Message.ERROR_PLAYER_ALREADY_WHITELISTED.toString().replace("{PLAYER}", player.getName()));
            return;
        }
        // Else, append player onto whitelist
        else
        {
            whitelist.add(player.getUniqueId());
        }


        // Reveal the hidden repeaters
//        AntiSkid.protMan.removePacketListener(plugin.blockChangeListener);
        if(diodes != null)
        {
            for(Location loc : diodes)
            {
                new RepeaterRevealPacket(loc).sendPacket(player); // Reveal repeaters
            }
        }
//        AntiSkid.protMan.addPacketListener(plugin.blockChangeListener);

        executor.sendMessage(Message.WHITELIST_ADD.toString().replace("{PLAYER}", player.getName()));
    }


    /**
     * Remove player from executor's whitelist
     * @param player player to remove
     */
    private void unWhitelistPlayer(Player player)
    {
        TreeSet<UUID> whitelist = plugin.whitelists.get(executorId);
        Set<Location> diodes = plugin.diodeMap.get(executorId);

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
            if(diodes != null)
            {
                for(Location loc : diodes)
                {
                    new RepeaterHidePacket(loc).sendPacket(player); // Hide repeaters
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
