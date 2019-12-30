package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.utils.PacketUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenPlayerJoin implements Listener
{
    private AntiSkid plugin;
    public ListenPlayerJoin(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        PacketUtil.loadPacketListener(new ListenBlockChangePacket(plugin, event.getPlayer()), event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        PacketUtil.unloadPacketListeners(event.getPlayer());
    }
}
