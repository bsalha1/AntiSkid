/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ListenPlayerLoginLogout implements Listener
{
    private Player player;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        this.player = event.getPlayer();
        AntiSkid.INSTANCE.getPacketManager().loadChannelListener(new ChannelListener(), player);
        Executors.newSingleThreadExecutor().submit(this::hideRepeaters);
    }

    private void hideRepeaters()
    {
        try
        {
            Thread.sleep(500);
        }
        catch(Exception ignored){}

        AntiSkid.INSTANCE.startSynchronousTask(()->
        {
            for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : AntiSkid.INSTANCE.diodes.entrySet())
            {
                if(AntiSkid.INSTANCE.whitelists.get(entry.getKey()).containsPlayer(player.getUniqueId())) continue;

                for(Set<Location> locs : entry.getValue().values())
                    for(Location loc : locs)
                    {
                        AntiSkid.INSTANCE.getNMS().sendBlockChangePacket(player, AntiSkid.INSTANCE.getReplacer(), loc);
                    }
            }
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        AntiSkid.INSTANCE.getPacketManager().unloadChannelListener(event.getPlayer());
    }
}
