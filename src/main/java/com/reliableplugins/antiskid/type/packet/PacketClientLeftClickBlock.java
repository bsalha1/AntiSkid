package com.reliableplugins.antiskid.type.packet;

import org.bukkit.Location;

public class PacketClientLeftClickBlock extends Packet
{
    private Location location;

    public PacketClientLeftClickBlock(Location location)
    {
        this.location = location;
    }

    public Location getLocation()
    {
        return location;
    }
}
