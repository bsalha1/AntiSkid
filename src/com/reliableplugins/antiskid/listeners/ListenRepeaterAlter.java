/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.runnables.ReplaceRepeater;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class ListenRepeaterAlter implements Listener
{
    private Main main;

    public ListenRepeaterAlter(Main main)
    {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRepeaterAlter(PlayerInteractEvent event)
    {
        // If event is not a right click onto an off diode, return
        if(event.getClickedBlock().getType() != Material.DIODE_BLOCK_OFF) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // If player has protected repeaters ...
        if(main.executors.contains(player))
        {
            for(Map.Entry<Player, Set<Block>> entry : main.diodeMap.entrySet())
            {
                // If this block is protected by someone
                if(entry.getValue().contains(block))
                {
                    if(entry.getKey().equals(player)) // If it's protected by the event player
                    {
                        break;
                    }
                    else // If it's protected by someone else
                    {
                        protectDiode(event, player, block);
                        return;
                    }
                }
            }

            // If the action isn't a right click on the diode, exit
            if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

            // If it's not protected, register it for the player and send the packet change
            if(!main.diodeMap.containsKey(player)) // If player doesn't have any registered diodes, register a new map
            {
                Set<Block> blockSet = new HashSet<>(Collections.singletonList(block));
                main.diodeMap.put(player, blockSet);
            }
            else // If the player already has registered diodes, add to the map
            {
                main.diodeMap.get(player).add(block);
            }

            // Broadcast packet change
            Collection<? extends Player> onlinePlayers = main.getServer().getOnlinePlayers();
            for(Player p : onlinePlayers)
            {
                if(p.equals(player)) continue; // <-- Whitelist here
                new ReplaceRepeater(this.main, block, p);
            }
        }
    }

    private void protectDiode(PlayerInteractEvent event, Player player, Block block)
    {
        event.setCancelled(true); // Cancel timing change
        player.sendMessage(Message.ERROR_PROTECTED_DIODE.toString());
        new ReplaceRepeater(this.main, block, player); // Replace repeater with carpet
    }
}
