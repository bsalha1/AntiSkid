/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.packets.RepeaterRevertPacket;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Set;

public class ListenPlayerLeave implements Listener
{
    private Main main;

    public ListenPlayerLeave(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if(this.main.tasks.containsKey(player)) // If player has active antiskid task, revert the repeater timings
        {
            this.main.tasks.get(player).cancel(); // Cancel the task

            Set<Block> blockSet = this.main.repeaterMap.get(player);
            Collection<? extends Player> onlinePlayers = this.main.getServer().getOnlinePlayers();
            for(Block b : blockSet)
            {
                for(Player p : onlinePlayers)
                {
                    new RepeaterRevertPacket(b).sendPacket(p);
                }
            }
        }

        // Remove player from the maps
        this.main.tasks.remove(player);
        this.main.repeaterMap.remove(player);
    }
}
