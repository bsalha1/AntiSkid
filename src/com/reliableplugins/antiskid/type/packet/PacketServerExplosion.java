package com.reliableplugins.antiskid.type.packet;

import com.reliableplugins.antiskid.type.Vector;

import java.util.Set;

public class PacketServerExplosion extends Packet
{
    private Set<Vector<Integer>> positions;

    public PacketServerExplosion(Set<Vector<Integer>> positions)
    {
        this.positions = positions;
    }

    public Set<Vector<Integer>> getPositions()
    {
        return positions;
    }
}
