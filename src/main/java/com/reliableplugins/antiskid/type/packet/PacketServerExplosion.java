package com.reliableplugins.antiskid.type.packet;

import org.bukkit.Location;

import java.util.Set;

public class PacketServerExplosion extends Packet
{
    private Set<Location> locations;

    public PacketServerExplosion(Set<Location> locations)
    {
        this.locations = locations;
    }

    public Set<Location> getLocations()
    {
        return locations;
    }
}
