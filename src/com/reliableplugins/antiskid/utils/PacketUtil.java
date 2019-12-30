package com.reliableplugins.antiskid.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil
{
    public static void unloadPacketListeners(Player player)
    {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() ->
        {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    private static void unloadPacketListener(ChannelDuplexHandler handler, Player player)
    {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() ->
        {
            channel.pipeline().remove(handler);
            return null;
        });
    }

    public static void loadPacketListener(ChannelDuplexHandler handler, Player player)
    {
        ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", player.getName(), handler);
    }

//    public static void unloadPacketListeners()
//    {
//    }
}
