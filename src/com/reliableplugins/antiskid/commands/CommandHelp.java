/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.enums.Message;
import org.bukkit.command.CommandSender;

@CommandBuilder(label = "help", alias = {"h"}, permission = "antiskid.help")
public class CommandHelp extends AbstractCommand
{
    @Override
    public void execute(CommandSender executor, String[] args)
    {
        executor.sendMessage(Message.HELP_ANTISKID.toString());
    }
}
