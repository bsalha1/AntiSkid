/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.Set;

public class ListenRepeaterBreak implements Listener
{
    private Main main;

    public ListenRepeaterBreak(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onRepeaterBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        if((block.getType() != Material.DIODE_BLOCK_OFF) && (block.getType() != Material.DIODE_BLOCK_ON)) return;

        // If block broken is a registered repeater, deregister it
        for(Map.Entry<Player, Set<Block>> entry : this.main.repeaterMap.entrySet())
        {
            if(entry.getValue().contains(block))
            {
                this.main.repeaterMap.get(entry.getKey()).remove(block);
            }
        }
    }
}
