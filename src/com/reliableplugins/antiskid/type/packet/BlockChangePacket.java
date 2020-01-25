package com.reliableplugins.antiskid.type.packet;

import com.reliableplugins.antiskid.type.Vector;
import org.bukkit.Material;

public class BlockChangePacket
{
    private Vector location;
    private Material material;

    public BlockChangePacket(Vector location, Material material)
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
