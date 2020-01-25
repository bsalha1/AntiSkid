package com.reliableplugins.antiskid.type.packet;

import org.bukkit.World;

public class MapChunkBulkPacket
{
    private int[] x;
    private int[] z;
    private World world;

    public MapChunkBulkPacket(int[] x, int[] z, World world)
    {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public int[] getX()
    {
        return x;
    }

    public int[] getZ()
    {
        return z;
    }

    public World getWorld()
    {
        return world;
    }
}
