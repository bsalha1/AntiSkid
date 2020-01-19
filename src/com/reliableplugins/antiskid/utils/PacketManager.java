/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.utils;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.PacketListener;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PacketManager
{
    private AntiSkid plugin;
    private Map<Player, Set<String>> handlers = new LinkedHashMap<>();

    public PacketManager(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Removes all packet handlers from the player
     * @param player the player to remove the handlers from
     */
    public void unloadAllPacketListeners(Player player)
    {
        Set<String> playerHandlers = handlers.get(player);
        if(playerHandlers.isEmpty())
        {
            return;
        }

        Channel channel = plugin.getNMS().getSocketChannel(player);
        for(String handlerName : playerHandlers)
        {
            if(channel.pipeline().get(handlerName) != null)
            {
                channel.pipeline().remove(handlerName);
            }
        }
        playerHandlers.clear();
    }



    /**
     * Removes all packet handlers from all players
     */
    public void unloadAllPacketListeners()
    {
        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        for(Player player : onlinePlayers)
            unloadAllPacketListeners(player);
    }



    /**
     * Removes a specific instance of a packet handler from the player
     * @param listener the instance of the handler to remove
     * @param player  the player to remove the handler from
     */
    private void unloadPacketListener(PacketListener listener, Player player)
    {
        Channel channel = plugin.getNMS().getSocketChannel(player);
        channel.pipeline().remove(listener.getClass().getName());
        handlers.computeIfAbsent(player, k -> new HashSet<>());
        handlers.get(player).remove(listener.getClass().getName());
    }



    /**
     * Removes a specific instance of a packet handler from all players
     * @param listener the instance of the handler to remove
     */
    private void unloadPacketListener(PacketListener listener)
    {
        Collection<? extends Player> onlinePlayers = this.plugin.getServer().getOnlinePlayers();
        for(Player player : onlinePlayers)
            unloadPacketListener(listener, player);
    }



    /**
     * Add an instance of a packet handler to a player. Held as the handler class name before packet_handler
     * @param listener the packet handler instance to be added
     * @param player  the player to receive the handler
     */
    public void loadPacketListener(PacketListener listener, Player player)
    {
        try
        {
            PacketListener listenerCopy = (PacketListener) listener.clone();
            listenerCopy.setPlayer(player);
            plugin.getNMS().getSocketChannel(player).pipeline().addBefore("packet_handler", listenerCopy.getClass().getName(), listenerCopy);

            handlers.computeIfAbsent(player, k -> new HashSet<>());
            handlers.get(player).add(listenerCopy.getClass().getName());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }



    /**
     * Add an instance of a packet handler to all players
     * @param listener the packet handler instance to be added
     */
    public void loadPacketListener(PacketListener listener)
    {
        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        for(Player player : onlinePlayers)
            loadPacketListener(listener, player);
    }

}
