/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.utils;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.listeners.ChannelListener;
import com.reliableplugins.antiskid.listeners.AChannelListener;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ChannelManager
{
    private AntiSkid plugin;

    public ChannelManager(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    public void unloadChannelListener(Player player)
    {
        plugin.getNMS().getSocketChannel(player).pipeline().remove(ChannelListener.class.getName());
    }

    public void unloadChannelListener()
    {
        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        for(Player player : onlinePlayers)
        {
            unloadChannelListener(player);
        }
    }

    public void loadChannelListener(AChannelListener listener, Player player)
    {
        try
        {
            AChannelListener listenerCopy = (AChannelListener) listener.clone();
            listenerCopy.setPlayer(player);
            plugin.getNMS().getSocketChannel(player).pipeline().addBefore("packet_handler", listenerCopy.getClass().getName(), listenerCopy);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void loadChannelListener(AChannelListener listener)
    {
        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        for(Player player : onlinePlayers)
        {
            loadChannelListener(listener, player);
        }
    }

}
