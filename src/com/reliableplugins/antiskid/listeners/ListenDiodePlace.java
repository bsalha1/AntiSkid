/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Diode;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ListenDiodePlace implements Listener
{
    private AntiSkid plugin;

    public ListenDiodePlace(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
//        if(!(event.getBlock() instanceof Diode)) return;
//
//        Chunk chunk = event.getBlock().getChunk();
//        UUID playerUUID = event.getPlayer().getUniqueId();
//        try
//        {
//            plugin.lock.acquire();
//        } catch(Exception ignored) { }
//        if(plugin.diodes.containsKey(playerUUID) && plugin.diodes.get(playerUUID).containsKey(chunk))
//        {
//            Location location = event.getBlock().getLocation();
//            plugin.diodes.get(playerUUID).get(chunk).add(location);
//            new AbstractTask(plugin, 1)
//            {
//                @Override
//                public void run()
//                {
//                    plugin.getNMS().broadcastBlockChangePacket(Material.CARPET, location, plugin.whitelists.get(playerUUID).getUUIDs());
//                }
//            };
//        }
//        plugin.lock.release();
    }
}
