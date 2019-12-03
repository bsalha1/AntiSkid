/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.reliableplugins.antiskid.abstracts.AbstractPacket;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class RepeaterHidePacket extends AbstractPacket
{
    private static final PacketType type = PacketType.Play.Server.BLOCK_CHANGE;
    private Block diode;

    public RepeaterHidePacket(Block diode)
    {
        super(new PacketContainer(type), type);
        this.diode = diode;
        this.handle.getModifier().writeDefaults();
        this.handle.getBlockData().write(0, WrappedBlockData.createData(Material.CARPET));
        this.handle.getBlockPositionModifier().write(0, new BlockPosition(diode.getLocation().toVector()));
    }

    @Override
    public void sendPacket(Player receiver)
    {
        EntityPlayer eReceiver = ((CraftPlayer) receiver).getHandle();
        WorldServer worldServer = ((CraftWorld) receiver.getWorld()).getHandle();
        Chunk chunk = this.diode.getChunk();

        // If the player doesn't have the chunk loaded... don't send the packet
        if(!worldServer.getPlayerChunkMap().a(eReceiver, chunk.getX(), chunk.getZ())) return;

        // Try sending packet to player
        try
        {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, getHandle());
        }
        catch(Exception e)
        {
            Bukkit.getConsoleSender().sendMessage("Failed to send blockchange packet: " + e.toString());
        }
    }
}
