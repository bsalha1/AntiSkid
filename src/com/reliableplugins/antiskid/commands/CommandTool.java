package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.type.SelectionTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandBuilder(label = "tool", alias = "t", permission = "antiskid.tool", description = "Get a region selection tool", playerRequired = true)
public class CommandTool extends Command
{
    @Override
    public void execute(CommandSender executor, String[] args)
    {
        Player player = (Player) executor;
        player.getInventory().addItem(SelectionTool.getItem());
    }
}
