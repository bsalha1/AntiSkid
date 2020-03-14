package com.reliableplugins.antiskid.type.packet;

import com.reliableplugins.antiskid.type.Vector;
import org.bukkit.Material;

public class PacketServerBlockChange extends Packet
{
    private Vector<Integer> position;
    private Material material;

    public PacketServerBlockChange(Vector<Integer> position, Material material)
    {
        this.position = position;
        this.material = material;
    }

    public Material getMaterial()
    {
        return material;
    }

    public Vector<Integer> getPosition()
    {
        return position;
    }
}
