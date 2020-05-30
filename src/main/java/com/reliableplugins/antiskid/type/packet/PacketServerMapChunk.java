package com.reliableplugins.antiskid.type.packet;

import org.bukkit.Chunk;

public class PacketServerMapChunk extends Packet
{
    private Chunk chunk;

    public PacketServerMapChunk(Chunk chunk)
    {
        this.chunk = chunk;
    }

    public Chunk getChunk()
    {
        return chunk;
    }
}
