package me.reachcarter.protocoltest.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;

public class RepeaterAlterPacket extends PacketContainer
{
    private Location location;

    public RepeaterAlterPacket(Location location)
    {
        this.type = PacketType.Play.Server.BLOCK_CHANGE;
        BlockPosition blockPos = new BlockPosition((int) location.getX(), (int) location.getY(), (int) location.getZ());
        WrappedBlockData blockData = new WrappedBlockData(Material.STONE);

        this.getModifier().writeDefaults();
        this.getBlockData().write(0, blockData);
        this.getBlockPositionModifier().write(0, blockPos);
    }
}
