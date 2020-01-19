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
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.Executors;

@CommandBuilder(label = "off", permission = "antiskid.off", playerRequired = true, description = "Turns off protection for the executor.\nRepeaters will be revealed to all players.")
public class CommandAntiskidOff extends AbstractCommand
{
    private Player executor;
    private UUID executorId;

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        this.executor = (Player) sender;
        this.executorId = executor.getUniqueId();

        if(!plugin.diodes.containsKey(executorId))
        {
            executor.sendMessage(Message.ERROR_NOT_PROTECTED.toString());
        }
        else
        {
            Executors.newSingleThreadExecutor().submit(this::antiskidOff);
        }
    }

    private void antiskidOff()
    {
        for(Chunk chunk : plugin.diodes.get(executorId).keySet())
        {
            Util.reloadChunk(chunk);
        }
        plugin.diodes.remove(executorId);
        executor.sendMessage(Message.ANTISKID_OFF.toString());
    }
}
