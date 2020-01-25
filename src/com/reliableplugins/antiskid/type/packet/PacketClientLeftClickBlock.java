package com.reliableplugins.antiskid.type.packet;

import com.reliableplugins.antiskid.type.Vector;

public class PacketClientLeftClickBlock extends Packet
{
    private Vector location;

    public PacketClientLeftClickBlock(Vector location)
    {
        this.location = location;
    }

    public Vector getLocation()
    {
        return location;
    }
}
