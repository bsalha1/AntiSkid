/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.annotation.CommandBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandBuilder(label = "reload", alias = "r", permission = "antiskid.reload", playerRequired = false, description = "Reload the AntiSkid config files")
public class CommandReload extends Command
{
    @Override
    public void execute(CommandSender executor, String[] args)
    {
//        plugin.getFileManager().saveAll();
        plugin.initConfigs();

        executor.sendMessage(ChatColor.AQUA + "AntiSkid has been reloaded");
    }
}
