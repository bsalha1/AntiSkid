package com.reliableplugins.antiskid.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;

public class RepeaterAlterPacket
{
    public PacketContainer packet;

    public RepeaterAlterPacket(Location location)
    {
        packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getModifier().writeDefaults();
        packet.getBlockData().write(0, WrappedBlockData.createData(Material.DIODE_BLOCK_OFF));
        packet.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));
    }
}
