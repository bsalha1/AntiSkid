/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.packets.RepeaterReplacePacket;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.Set;

public class ListenPlayerJoin implements Listener
{
    private Main main;

    public ListenPlayerJoin(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        for(Map.Entry<Player, Set<Block>> entry : main.diodeMap.entrySet())
        {
            for(Block b : entry.getValue())
            {
                new RepeaterReplacePacket(b).sendPacket(player);
            }
        }
    }

}
