/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Base_CommandAntiSkid implements CommandExecutor
{
    public Map<String, AbstractCommand> subcommands = new HashMap<>();

    private CommandHelp commandHelp;

    private AntiSkid plugin;

    public Base_CommandAntiSkid(AntiSkid plugin)
    {
        this.plugin = plugin;
        this.commandHelp = new CommandHelp();

        addCommand(new CommandAntiskidOn());
        addCommand(new CommandAntiskidOff());
        addCommand(new CommandWhitelist());
        plugin.getCommand("antiskid").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] args) {

        if(args.length == 0)
        {
            if(commandSender.hasPermission(commandHelp.getPermission()))
            {
                commandHelp.execute(commandSender, null);
            }
            else
            {
                commandSender.sendMessage(Message.ERROR_NO_PERMS.toString());
            }
            return true;
        }

        for (AbstractCommand subcommand : subcommands.values())
        {
            // If argument isn't a subcommand or an alias of a subcommand, continue
            if(!args[0].equalsIgnoreCase(subcommand.getLabel()) && !subcommand.getAlias().contains(args[0].toLowerCase())) continue;

            // If player is required and they're not a player, throw error
            if(subcommand.isPlayerRequired() && !(commandSender instanceof Player))
            {
                commandSender.sendMessage(Message.ERROR_NOT_PLAYER.toString());
                return true;
            }

            // If first argument is a valid subcommand or is a subcommand alias, run command
            if(args[0].equalsIgnoreCase(subcommand.getLabel()) || subcommand.getAlias().contains(args[0].toLowerCase()))
            {
                // If subcommand doesn't have a permission node or the send has permission or they're an op, execute command
                if(!subcommand.hasPermission() || commandSender.hasPermission(subcommand.getPermission()) || commandSender.isOp())
                {
                    subcommand.execute(commandSender, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                }
                else
                {
                    commandSender.sendMessage(Message.ERROR_NO_PERMS.toString());
                    return true;
                }
            }
        }

        // By here, the command entered isn't valid
        commandHelp.execute(commandSender, null);
        return true;
    }

    public void addCommand(AbstractCommand command)
    {
        command.setPlugin(plugin);
        this.subcommands.put(command.getLabel().toLowerCase(), command);
    }

    public JavaPlugin getPlugin()
    {
        return plugin;
    }

    public Collection<AbstractCommand> getCommands()
    {
        return Collections.unmodifiableCollection(subcommands.values());
    }
}
