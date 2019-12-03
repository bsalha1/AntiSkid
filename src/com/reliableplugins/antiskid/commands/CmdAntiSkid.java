/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.hook.impl.FactionHook;
import com.reliableplugins.antiskid.items.AntiSkidTool;
import com.reliableplugins.antiskid.packets.RepeaterReplacePacket;
import com.reliableplugins.antiskid.packets.RepeaterRevertPacket;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

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

        if(strings.length > 2) // Too many arguments
        {
            executor.sendMessage(Message.HELP_ANTISKID.toString());
            return true;
        }

        /* GET TOOL */
        if(strings[0].equalsIgnoreCase("tool"))
        {
            antiskidTool();
            return true;
        }


        /* ANTISKID ON */
        else if(strings[0].equalsIgnoreCase("on"))
        {
            Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().build()).submit(this::antiskidOn); // Run asynchronously
            return true;
        }


        /* ANTISKID OFF */
        else if(strings[0].equalsIgnoreCase("off"))
        {
            Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().build()).submit(this::antiskidOff); // Run asynchronously
            return true;
        }


        /* WHITELIST */
        else if(strings[0].equalsIgnoreCase("whitelist"))
        {
            if(strings.length == 2) // Adding player to whitelist
            {
                Bukkit.getPlayer(strings[1]);
            }
            else
            {
                if(!main.whitelists.containsKey(executor))
                {
                    return true;
                }
                else
                {

                }
            }
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
            Location loc;
            Block block;
            Set<Block> diodes = new HashSet<>();

            int x1 = (int) point1.getX();
            int x2 = (int) point2.getX();
            int diffX = sign(x2 - x1);

            int y1 = (int) point1.getY();
            int y2 = (int) point2.getY();
            int diffY = sign(y2 - y1);

            int z1 = (int) point1.getZ();
            int z2 = (int) point2.getZ();
            int diffZ = sign(z2 - z1);

            int count = 0;

            for(int z = z1; diffZ > 0? (z <= z2) : (z >= z2); z += diffZ)
            {
                for(int y = y1; diffY > 0? (y <= y2) : (y >= y2); y += diffY)
                {
                    for(int x = x1; diffX > 0? (x <= x2) : (x >= x2); x += diffX)
                    {
                        loc = new Location(world, x, y, z);
                        if(!FactionHook.canBuild(executor, loc.getChunk()))
                        {
                            executor.sendMessage(Message.ERROR_NOT_TERRITORY.toString());
                            return;
                        }
                        block = loc.getBlock();
                        if(block.getType().equals(Material.DIODE_BLOCK_OFF))
                        {
                            diodes.add(block);
                            count++;
                        }
                    }
                }
            }

            // If whitelist hasn't already been populated, populate it with the executor
            if(!main.whitelists.containsKey(executor))
            {
                main.whitelists.put(executor, new HashSet<>(Collections.singletonList(executor)));
            }

            // Change diodes to carpets for all players not in whitelist
            for(Block b : diodes)
            {
                new RepeaterReplacePacket(b).broadcastPacket(main.whitelists.get(executor));
            }

            // Store diodes
            main.diodeMap.put(executor, diodes);

            executor.sendMessage(String.format(Message.ANTISKID_ON.toString(), count));
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
