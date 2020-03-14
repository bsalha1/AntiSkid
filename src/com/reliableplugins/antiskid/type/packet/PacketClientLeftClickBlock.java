package com.reliableplugins.antiskid.type.packet;

import com.reliableplugins.antiskid.type.Vector;

public class PacketClientLeftClickBlock extends Packet
{
    private Vector<Integer> position;

    public PacketClientLeftClickBlock(Vector<Integer> position)
    {
        this.position = position;
    }

    public Vector<Integer> getPosition()
    {
        return position;
    }
}
