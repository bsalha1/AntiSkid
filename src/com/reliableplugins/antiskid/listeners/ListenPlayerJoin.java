/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ListenPlayerJoin implements Listener
{
    private AntiSkid plugin;
    private Player player;

    public ListenPlayerJoin(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        this.player = event.getPlayer();
        plugin.packMan.loadPacketListener(new ListenBlockChangePacket(plugin), player);
        Executors.newSingleThreadExecutor().submit(this::hideRepeaters);
    }

    private void hideRepeaters()
    {
        try
        {
            Thread.sleep(500);
        }
        catch(Exception ignored){}

        try
        {
            plugin.lock.acquire();
        }
        catch(Exception ignored) { }
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
        {
            if(plugin.whitelists.get(entry.getKey()).containsPlayer(player.getUniqueId())) continue;

            for(Set<Location> locs : entry.getValue().values())
                for(Location loc : locs)
                {
                    plugin.getNMS().sendBlockChangePacket(player, Material.CARPET, loc);
                }
        }
        plugin.lock.release();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        plugin.packMan.unloadAllPacketListeners(event.getPlayer());
    }
}
