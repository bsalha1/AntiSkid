/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.config.Message;
import com.reliableplugins.antiskid.utils.BukkitUtil;
import com.reliableplugins.antiskid.utils.ReflectUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.*;

@CommandBuilder(label = "clear", description = "Reveal all protected repeaters", permission = "antiskid.clear")
public class CommandClear extends Command
{
    @Override
    public void execute(CommandSender executor, String[] args)
    {
        plugin.startSynchronousTask(()->
        {
            int i = 0;
            TreeMap<UUID, Map<Chunk, Set<Location>>> diodes = (TreeMap<UUID, Map<Chunk, Set<Location>>>) plugin.diodes.clone();
            plugin.diodes.clear();
            for(Map<Chunk, Set<Location>> map : diodes.values())
            {
                for(Chunk chunk : map.keySet())
                {
                    BukkitUtil.reloadChunk(chunk);
                    i++;
                }
            }
            executor.sendMessage(Message.ANTISKID_CLEAR.getMessage().replace("{NUM}", Integer.toString(i)));
        });

    }
}
