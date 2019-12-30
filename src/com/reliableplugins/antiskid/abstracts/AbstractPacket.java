/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.abstracts;

import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;

public abstract class AbstractPacket
{
    protected Packet packet;

    public void sendPacket(Player receiver)
    {
        ((CraftPlayer) receiver).getHandle().playerConnection.sendPacket(this.packet);
    }

    public void sendPacket(EntityPlayer receiver)
    {
        receiver.playerConnection.sendPacket(this.packet);
    }

    public void broadcastPacket()
    {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for(Player p : onlinePlayers)
        {
            sendPacket(p);
        }
    }

    public void broadcastPacket(TreeSet<UUID> whitelist)
    {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for(Player p : onlinePlayers)
        {
            if(whitelist.contains(p.getUniqueId())) continue;
            sendPacket(p);
        }
    }
}
