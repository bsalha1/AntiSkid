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
        Material material = event.getBlock().getType();
        if((material != Material.DIODE_BLOCK_OFF) && (material != Material.DIODE_BLOCK_ON)) return;

        Set<Map.Entry<Player, Set<Block>>> entries = main.diodeMap.entrySet();
        Block block = event.getBlock();

        // If block broken is a registered diode, deregister it
        for(Map.Entry<Player, Set<Block>> entry : entries)
        {
            if(entry.getValue().contains(block))
            {
                this.main.diodeMap.get(entry.getKey()).remove(block);
            }
        }
    }
}
