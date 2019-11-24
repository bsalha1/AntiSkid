/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.packets.RepeaterReplacePacket;
import com.reliableplugins.antiskid.packets.RepeaterRevertPacket;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

public class CmdAntiSkid implements CommandExecutor
{
    private Main main;
    private Player executor;

    public CmdAntiSkid(Main main)
    {
        this.main = main;
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

        if(strings.length != 1) // Not enough args
        {
            executor.sendMessage(Message.HELP_ANTISKID.toString());
            return true;
        }


        /* ANTISKID ON */
        if(strings[0].equalsIgnoreCase("on"))
        {
            antiskidOn();
        }


        /* ANTISKID OFF */
        else if(strings[0].equals("off"))
        {
            antiskidOff();
        }


        /* BAD ARGUMENT */
        else
        {
            executor.sendMessage(Message.HELP_ANTISKID.toString());
        }



        return true;
    }



    /**
     * Turns antiskid protection on
     */
    private void antiskidOn()
    {
        this.main.executors.add(executor);

        Set<Block> blockSet = this.main.diodeMap.get(executor);
        if(blockSet == null)
        {
            executor.sendMessage(Message.ANTISKID_ON.toString());
            return;
        }
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        for(Block b : blockSet) // Foreach repeater placed by executor
        {
            for (Player p : onlinePlayers) // Foreach online player
            {
                // Whitelist here
                if (p.equals(executor)) continue;
                new RepeaterReplacePacket(b).sendPacket(p); // Replace diode with carpet
            }
        }
        executor.sendMessage(Message.ANTISKID_ON.toString());
    }



    /**
     * Turns antiskid protection off
     */
    private void antiskidOff()
    {
        if(this.main.diodeMap.get(executor) == null) // If there are no diodes registered
        {
            executor.sendMessage(Message.ANTISKID_OFF.toString());
            return;
        }

        Collection<? extends Player> onlinePlayers = this.main.getServer().getOnlinePlayers();
        Set<Block> blockSet = this.main.diodeMap.get(executor);

        // Revert diodes back to their original state
        for(Player p : onlinePlayers)
        {
            for(Block b : blockSet) // Foreach diode registered to the protected executor
            {
                new RepeaterRevertPacket(b).sendPacket(p); // Revert the diode
            }
        }
        this.main.diodeMap.remove(executor);
        this.main.executors.remove(executor);
        executor.sendMessage(Message.ANTISKID_OFF.toString());
    }
}
