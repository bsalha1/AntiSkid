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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ListenRepeaterPlace implements Listener
{
    private Main main;

    public ListenRepeaterPlace(Main main)
    {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(PlayerInteractEvent event)
    {
        // If event is not a right click onto an off diode, return
        if((event.getAction() != Action.RIGHT_CLICK_BLOCK) || (event.getClickedBlock().getType() != Material.DIODE_BLOCK_OFF)) return;



        Block block = event.getClickedBlock();
        Player player = event.getPlayer();



        // If the player is not already registered, register it
        if(!main.repeaterMap.containsKey(player))
        {
            Set<Block> blockSet = new HashSet<>(Arrays.asList(block));
            main.repeaterMap.put(player, blockSet);
            return;
        }



        // Add the diode to the player
        main.repeaterMap.get(player).add(block);
    }
}
