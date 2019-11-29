/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.items.AntiSkidTool;
import com.reliableplugins.antiskid.listeners.ListenBlockChangePacket;
import com.reliableplugins.antiskid.packets.RepeaterReplacePacket;
import com.reliableplugins.antiskid.packets.RepeaterRevertPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        /* GET TOOL */
        if(strings[0].equalsIgnoreCase("tool"))
        {
            antiskidTool();
        }


        /* ANTISKID ON */
        else if(strings[0].equalsIgnoreCase("on"))
        {
            Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().build()).submit(this::antiskidOn); // Run asynchronously
        }


        /* ANTISKID OFF */
        else if(strings[0].equalsIgnoreCase("off"))
        {
            Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().build()).submit(this::antiskidOff); // Run asynchronously
        }


        /* BAD ARGUMENT */
        else
        {
            executor.sendMessage(Message.HELP_ANTISKID.toString());
        }



        return true;
    }



    /**
     * Gives executor the antiskid tool
     */
    private void antiskidTool()
    {
        this.main.executors.add(executor);
        new AntiSkidTool().give(executor);
        executor.sendMessage(Message.HELP_TOOL.toString());
    }


    /**
     * Turns antiskid protection on. Parse region for repeaters and broadcast a packet change for all blacklisted players
     */
    private void antiskidOn()
    {
        // If player either hasn't selected a region, has no position #1 or has no position #2, error
        if(!main.toolPoints.containsKey(executor) || (main.toolPoints.get(executor).getKey() == null) || (main.toolPoints.get(executor).getValue() == null))
        {
            executor.sendMessage(Message.ERROR_NO_REGION.toString());
            return;
        }
        else
        {
            World world = executor.getWorld();
            Location point1 = main.toolPoints.get(executor).getKey();
            Location point2 = main.toolPoints.get(executor).getValue();
            Set<Block> diodes = new HashSet<>();
            Block block;
            Set<Player> whitelist = new HashSet<>();
            whitelist.add(executor);

            int x1 = (int) point1.getX();
            int x2 = (int) point2.getX();

            int y1 = (int) point1.getY();
            int y2 = (int) point2.getY();

            int z1 = (int) point1.getZ();
            int z2 = (int) point2.getZ();


            for(int z = z1; sign(z2 - z1) > 0? (z < z2) : (z > z2); z += sign(z2 - z1))
            {
                for(int y = y1; sign(y2 - y1) > 0? (y < y2) : (y > y2); y += sign(y2 - y1))
                {
                    for(int x = x1; sign(x2 - x1) > 0? (x < x2) : (x > x2); x += sign(x2 - x1))
                    {
                        block = new Location(world, x, y, z).getBlock();
                        if(block.getType().equals(Material.DIODE_BLOCK_OFF))
                        {
                            diodes.add(block);
                            new RepeaterReplacePacket(block).broadcastPacket(whitelist);
                            i++;
                        }
                        executor.sendBlockChange(block.getLocation(), Material.GLASS, (byte) 0);
                    }
                }
                try { TimeUnit.MILLISECONDS.sleep(1); } catch (InterruptedException ignored) {}
            }
            main.diodeMap.put(executor, diodes);
            executor.sendMessage(Message.ANTISKID_ON.toString());
        }
    }

    private int sign(int number)
    {
        return (number == 0)? 1 : number / Math.abs(number);
    }


    /**
     * Turns antiskid protection off
     */
    private void antiskidOff()
    {
        if(!main.diodeMap.containsKey(executor)) // If there are no diodes registered
        {
            executor.sendMessage(Message.ERROR_NO_PROTECTED.toString());
            return;
        }

        Set<Block> blockSet = main.diodeMap.get(executor);
        Set<Player> whitelist = new HashSet<>();
        whitelist.add(executor);

        main.protMan.removePacketListener(main.blockChangeListener);
        for(Block b : main.diodeMap.get(executor)) new RepeaterRevertPacket(b).broadcastPacket(whitelist); // Revert the diode for all blacklisted players
        main.protMan.addPacketListener(main.blockChangeListener);

        main.diodeMap.remove(executor);
        main.executors.remove(executor);
        executor.sendMessage(Message.ANTISKID_OFF.toString());
    }
}
