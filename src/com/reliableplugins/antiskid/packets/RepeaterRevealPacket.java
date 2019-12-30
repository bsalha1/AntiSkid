/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.packets;

import com.reliableplugins.antiskid.abstracts.AbstractPacket;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

public class RepeaterRevealPacket extends AbstractPacket
{
    private Location location;

    public RepeaterRevealPacket(Location location)
    {
        this.location = location;
        this.packet = new PacketPlayOutBlockChange(
                ((CraftWorld) location.getWorld()).getHandle(),
                new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));

        ((PacketPlayOutBlockChange) this.packet).block = CraftMagicNumbers
                .getBlock(location.getBlock().getType())
                .fromLegacyData(location.getBlock().getData());
    }

    @Override
    public void sendPacket(Player receiver)
    {
        EntityPlayer eReceiver = ((CraftPlayer) receiver).getHandle();
        WorldServer worldServer = ((CraftWorld) receiver.getWorld()).getHandle();
        Chunk chunk = location.getChunk();

        // If the player doesn't have the chunk loaded... don't send the packet
        if(!worldServer.getPlayerChunkMap().a(eReceiver, chunk.getX(), chunk.getZ())) return;

        super.sendPacket(eReceiver);
    }
}
