/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.config.Message;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandHandler implements CommandExecutor
{
    private Map<String, Command> subcommands = new HashMap<>();
    private CommandHelp commandHelp;
    private AntiSkid plugin;

    public CommandHandler(AntiSkid plugin)
    {
        this.plugin = plugin;
        this.commandHelp = new CommandHelp(this);
        plugin.getCommand("antiskid").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String string, String[] args) {

        if(args.length == 0)
        {
            if(commandSender.hasPermission(commandHelp.getPermission()))
            {
                commandHelp.execute(commandSender, args);
            }
            else
            {
                commandSender.sendMessage(Message.ERROR_NO_PERMS.getMessage());
            }
            return true;
        }

        for (Command subcommand : subcommands.values())
        {
            // If argument isn't a subcommand or an alias of a subcommand, continue
            if(!args[0].equalsIgnoreCase(subcommand.getLabel()) && !subcommand.getAlias().contains(args[0].toLowerCase())) continue;

            // If player is required and they're not a player, throw error
            if(subcommand.isPlayerRequired() && !(commandSender instanceof Player))
            {
                commandSender.sendMessage(Message.ERROR_NOT_PLAYER.getMessage());
                return true;
            }

            // If first argument is a valid subcommand or is a subcommand alias, run command
            if(args[0].equalsIgnoreCase(subcommand.getLabel()) || subcommand.getAlias().contains(args[0].toLowerCase()))
            {
                // If subcommand doesn't have a permission node or the send has permission or they're an op, execute command
                if(!subcommand.hasPermission() || commandSender.hasPermission(subcommand.getPermission()) || commandSender.isOp())
                {
                    subcommand.execute(commandSender, Arrays.copyOfRange(args, 1, args.length));
                }
                else
                {
                    commandSender.sendMessage(Message.ERROR_NO_PERMS.getMessage());
                }
                return true;
            }
        }

        // By here, the command entered isn't valid
        commandHelp.execute(commandSender, args);
        return true;
    }

    public void addCommand(Command command)
    {
        command.setPlugin(plugin);
        this.subcommands.put(command.getLabel().toLowerCase(), command);
    }

    public Collection<Command> getCommands()
    {
        return Collections.unmodifiableCollection(subcommands.values());
    }
}
