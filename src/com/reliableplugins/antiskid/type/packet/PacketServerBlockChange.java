package com.reliableplugins.antiskid.type.packet;

import com.reliableplugins.antiskid.type.Vector;
import org.bukkit.Material;

public class PacketServerBlockChange extends Packet
{
    private Vector location;
    private Material material;

    public PacketServerBlockChange(Vector location, Material material)
    {
        this.location = location;
        this.material = material;
    }

    public Material getMaterial()
    {
        return material;
    }

    public Vector getLocation()
    {
        return location;
    }
}
