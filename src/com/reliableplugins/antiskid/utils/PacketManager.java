/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.utils;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.PacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public class PacketManager
{
    private AntiSkid plugin;

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
        Channel channel = plugin.getNMS().getSocketChannel(player);
        channel.eventLoop().submit(() ->
        {
            for(Map.Entry<String, ChannelHandler> entry : channel.pipeline().toMap().entrySet())
            {
                if(entry.getValue() instanceof ChannelDuplexHandler)
                {
                    channel.pipeline().remove(entry.getKey());
                }
            }
            return null;
        });
    }



    /**
     * Removes all packet handlers from all players
     */
    public void unloadAllPacketListeners()
    {
        Collection<? extends Player> onlinePlayers = this.plugin.getServer().getOnlinePlayers();
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
        channel.eventLoop().submit(() ->
        {
            channel.pipeline().remove(listener);
            return null;
        });
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
            plugin.getNMS().getSocketChannel(player).pipeline()
                    .addBefore("packet_handler", listener.getClass().getName(), listenerCopy);
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
        Collection<? extends Player> onlinePlayers = this.plugin.getServer().getOnlinePlayers();
        for(Player player : onlinePlayers)
            loadPacketListener(listener, player);
    }

}
