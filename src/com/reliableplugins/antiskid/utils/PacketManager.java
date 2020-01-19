package com.reliableplugins.antiskid.utils;

import com.reliableplugins.antiskid.abstracts.PacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public class PacketManager
{
    private Server server;

    public PacketManager(Server server)
    {
        this.server = server;
    }



    /**
     * Removes all packet handlers from the player
     * @param player the player to remove the handlers from
     */
    public void unloadAllPacketListeners(Player player)
    {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() ->
        {
            for(Map.Entry<String, ChannelHandler> entry : channel.pipeline().toMap().entrySet())
            {
                if(entry.getValue() instanceof ChannelDuplexHandler)
                {
                    channel.pipeline().remove(entry.getKey());
                    Bukkit.broadcastMessage("Removed");
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
        Collection<? extends Player> onlinePlayers = this.server.getOnlinePlayers();
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
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
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
        Collection<? extends Player> onlinePlayers = this.server.getOnlinePlayers();
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
            ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline()
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
        Collection<? extends Player> onlinePlayers = this.server.getOnlinePlayers();
        for(Player player : onlinePlayers)
            loadPacketListener(listener, player);
    }

}
