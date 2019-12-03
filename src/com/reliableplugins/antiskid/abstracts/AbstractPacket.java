/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * In abidance with GNU GPL: Changes made to this file: removed files and altered bracket places and spacing
 */
package com.reliableplugins.antiskid.abstracts;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;

public abstract class AbstractPacket
{
    protected PacketContainer handle;

    protected AbstractPacket(PacketContainer handle, PacketType type)
    {
        if (handle == null) throw new IllegalArgumentException("Packet handle cannot be NULL.");
        if (!Objects.equal(handle.getType(), type)) throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);
        this.handle = handle;
    }

    public PacketContainer getHandle()
    {
        return handle;
    }


    /**
     * Send packet to a player
     * @param receiver player to receive the packet
     */
    public void sendPacket(Player receiver)
    {
        try
        {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, getHandle());
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }


    /**
     * Send packet to all online players not in whitelist
     * @param whitelist players to not receive packet
     */
    public void broadcastPacket(Set<Player> whitelist)
    {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for(Player p : onlinePlayers)
        {
            if(whitelist.contains(p)) continue;
            this.sendPacket(p);
        }
    }


    /**
     * Send packet to all online players
     */
    public void broadcastPacket()
    {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for(Player p : onlinePlayers)
        {
            this.sendPacket(p);
        }
    }


    /**
     * Mimic receiving packet
     * @param sender player sending packet
     */
    public void receivePacket(Player sender)
    {
        try
        {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, getHandle());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot receive packet.", e);
        }
    }
}
