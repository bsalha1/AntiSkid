/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.packets.RepeaterRevertPacket;
import com.reliableplugins.antiskid.runnables.MaskRepeaters;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        // If executor already registered their diodes, throw error and exit command
        if(this.main.tasks.containsKey(executor))
        {
            executor.sendMessage(Message.ERROR_ALREADY_PROTECTED.toString());
            return;
        }

        // Else begin the antiskid and register the task with the plugin
        MaskRepeaters maskTask = new MaskRepeaters(this.main, executor);
        this.main.tasks.put(executor, maskTask);
        executor.sendMessage(Message.ANTISKID_ON.toString());
    }



    /**
     * Turns antiskid protection off
     */
    private void antiskidOff()
    {
        // If executor has not executed /antiskid, throw error and quit command
        if(!this.main.tasks.containsKey(executor))
        {
            executor.sendMessage(Message.ERROR_NOT_PROTECTED.toString());
            return;
        }

        // Cancel the task and remove from list
        this.main.tasks.get(executor).cancel();
        this.main.tasks.remove(executor);

        // Revert diodes back to their original state
        for(Player p : this.main.getServer().getOnlinePlayers())
        {
            if(this.main.repeaterMap.get(executor) == null) return; // If there are no diodes registered

            for(Block b : this.main.repeaterMap.get(executor)) // Foreach diode registered to the protected executor
            {
                new RepeaterRevertPacket(b).sendPacket(p); // Revert the diode
            }
        }
        this.main.repeaterMap.remove(executor);
        executor.sendMessage(Message.ANTISKID_OFF.toString());
    }
}
