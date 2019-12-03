/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.packets.RepeaterHidePacket;
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@CommandBuilder(label = "whitelist", permission = "antiskid.whitelist")
public class CommandWhitelist extends AbstractCommand
{
    private Player executor;

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        this.executor = (Player) sender;

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
        Set<Player> whitelist = plugin.whitelists.get(executor);

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
     * Adds a player to executor's whitelist
     * @param player player to add
     */
    private void whitelistPlayer(Player player)
    {
        Set<Player> whitelist = plugin.whitelists.get(executor);
        Set<Block> diodes = plugin.diodeMap.get(executor);

        // If invalid player... throw error
        if(player == null)
        {
            executor.sendMessage(Message.ERROR_INVALID_PLAYER.toString());
            return;
        }

        // If the whitelist isn't initialized, initialize it
        if(whitelist == null)
        {
            plugin.whitelists.put(executor, new HashSet<>(Arrays.asList(executor, player)));
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
        AntiSkid.protMan.removePacketListener(plugin.blockChangeListener);
        if(diodes != null)
        {
            for(Block b : diodes) new RepeaterRevealPacket(b).sendPacket(player); // Reveal repeaters
        }
        AntiSkid.protMan.addPacketListener(plugin.blockChangeListener);

        executor.sendMessage(String.format(Message.WHITELISTED.toString(), player.getName()));
    }


    /**
     * Remove player from executor's whitelist
     * @param player player to remove
     */
    private void unWhitelistPlayer(Player player)
    {
        Set<Player> whitelist = plugin.whitelists.get(executor);
        Set<Block> diodes = plugin.diodeMap.get(executor);

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
        }
        // If whitelist doesn't contain player... throw error
        else
        {
            executor.sendMessage(String.format(Message.ERROR_PLAYER_NOT_WHITELISTED.toString(), player.getName()));
        }
    }
}
