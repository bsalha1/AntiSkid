/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.utils.Util;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

@CommandBuilder(label = "off", permission = "antiskid.off", playerRequired = true, description = "Turns off protection for the executor.\nRepeaters will be revealed to all players.")
public class CommandOff extends AbstractCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        Player executor = (Player) sender;
        UUID executorId = executor.getUniqueId();

        if(!plugin.diodes.containsKey(executorId))
        {
            executor.sendMessage(plugin.getMessageManager().ERROR_NOT_PROTECTED);
        }
        else
        {
            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) { }

            Set<Chunk> chunks = plugin.diodes.get(executorId).keySet();
            for(Chunk chunk : chunks)
            {
                Util.reloadChunk(chunk);
            }
            plugin.diodes.remove(executorId);
            plugin.lock.release();

            executor.sendMessage(plugin.getMessageManager().ANTISKID_OFF);
        }
    }
}
