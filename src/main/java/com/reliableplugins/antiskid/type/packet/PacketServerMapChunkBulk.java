package com.reliableplugins.antiskid.type.packet;

import org.bukkit.Chunk;
import org.bukkit.World;

public class PacketServerMapChunkBulk extends Packet
{
    private World world;
    private Chunk[] chunks;

    public PacketServerMapChunkBulk(int[] x, int[] z, World world)
    {
        this.world = world;

        chunks = new Chunk[x.length];
        for(int i = 0; i < x.length; i++)
        {
            chunks[i] = world.getChunkAt(x[i], z[i]);
        }
    }

    public Chunk[] getChunks()
    {
        return chunks;
    }

    public World getWorld()
    {
        return world;
    }
}
