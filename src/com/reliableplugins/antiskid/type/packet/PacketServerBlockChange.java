package com.reliableplugins.antiskid.type.packet;

import com.reliableplugins.antiskid.type.Vector;
import org.bukkit.Location;
import org.bukkit.Material;

public class PacketServerBlockChange extends Packet
{
    private Location location;
    private Material material;

    public PacketServerBlockChange(Location location, Material material)
    {
        this.location = location;
        this.material = material;
    }

    public Material getMaterial()
    {
        return material;
    }

    public Location getLocation()
    {
        return location;
    }
}
