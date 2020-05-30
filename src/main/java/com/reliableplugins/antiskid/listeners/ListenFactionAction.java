/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.listeners;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.utils.BukkitUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ListenFactionAction implements Listener
{
    @EventHandler
    public void onUnclaim(LandUnclaimEvent event)
    {
        Chunk chunk = event.getLocation().getChunk();
        AntiSkid.INSTANCE.startSynchronousTask(()->
        {
            for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : AntiSkid.INSTANCE.diodes.entrySet())
            {
                if(entry.getValue().containsKey(chunk))
                {
                    AntiSkid.INSTANCE.diodes.get(entry.getKey()).remove(chunk);
                    BukkitUtil.reloadChunk(chunk);
                    return;
                }
            }
        });
    }

    @EventHandler
    public void onUnclaimAll(LandUnclaimAllEvent event)
    {
        AntiSkid.INSTANCE.startSynchronousTask(()->
        {
            for(FLocation floc : event.getFaction().getAllClaims())
            {
                Chunk chunk = floc.getChunk();
                for(Map<Chunk, Set<Location>> map : AntiSkid.INSTANCE.diodes.values())
                {
                    map.remove(chunk);
                    BukkitUtil.reloadChunk(chunk);
                }
            }
        });
    }

    @EventHandler
    public void onJoinFaction(FPlayerJoinEvent event)
    {
        if(AntiSkid.INSTANCE.getMainConfig().whitelistFaction)
        {
            for(Player player : event.getFaction().getOnlinePlayers())
            {
                if(AntiSkid.INSTANCE.whitelists.containsKey(player.getUniqueId()))
                {
                    AntiSkid.INSTANCE.startSynchronousTask(()-> AntiSkid.INSTANCE.whitelists.get(player.getUniqueId()).addPlayer(event.getfPlayer().getPlayer()));
                    if(AntiSkid.INSTANCE.diodes.containsKey(player.getUniqueId()))
                    {
                        for(Chunk chunk : AntiSkid.INSTANCE.diodes.get(player.getUniqueId()).keySet())
                        {
                            BukkitUtil.reloadChunk(chunk);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeaveFaction(FPlayerLeaveEvent event)
    {
        if(AntiSkid.INSTANCE.getMainConfig().whitelistFaction)
        {
            Player player = event.getfPlayer().getPlayer();

            AntiSkid.INSTANCE.startSynchronousTask(()->
            {
                for(Map.Entry<UUID, Whitelist> entry : AntiSkid.INSTANCE.whitelists.entrySet())
                {
                    Whitelist whitelist = entry.getValue();
                    if(!whitelist.getCreator().equals(player) && whitelist.containsPlayer(player))
                    {
                        AntiSkid.INSTANCE.whitelists.get(entry.getKey()).removePlayer(player);
                        Map<Chunk, Set<Location>> diodes = AntiSkid.INSTANCE.diodes.get(entry.getKey());
                        if(diodes != null)
                        {
                            for(Chunk chunk : diodes.keySet())
                            {
                                BukkitUtil.reloadChunk(chunk);
                            }
                        }
                    }
                }
            });
        }
    }


}
