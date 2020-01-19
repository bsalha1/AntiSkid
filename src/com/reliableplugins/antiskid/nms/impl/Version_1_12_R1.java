package com.reliableplugins.antiskid.nms.impl;

import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.type.Vector;
import net.minecraft.server.v1_12_R1.BlockDiodeAbstract;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;

public class Version_1_12_R1 implements INMSHandler
{
    @Override
    public String getVersion()
    {
        return "v1_12_R1";
    }

    @Override
    public void sendBlockChangePacket(Player player, Material material, Location location)
    {
        PacketPlayOutBlockChange packet =  new PacketPlayOutBlockChange(
                ((CraftWorld) location.getWorld()).getHandle(),
                new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));

        packet.block = CraftMagicNumbers.getBlock(material).fromLegacyData((byte) 0);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void broadcastBlockChangePacket(Material material, Location location, TreeSet<UUID> whitelist)
    {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for(Player player : onlinePlayers)
        {
            if(whitelist.contains(player.getUniqueId())) continue;
            sendBlockChangePacket(player, material, location);
        }
    }

    @Override
    public Vector getLocation(Object packet) throws IllegalAccessException, NoSuchFieldException
    {
        PacketPlayOutBlockChange blockChange = (PacketPlayOutBlockChange) packet;
        BlockPosition bpos;

        Field field = PacketPlayOutBlockChange.class.getDeclaredField("a");
        field.setAccessible(true);
        bpos = (BlockPosition) field.get(blockChange);

        return new Vector(bpos.getX(), bpos.getY(), bpos.getZ());
    }

    @Override
    public boolean isDiodeBlockChangePacket(Object packet)
    {
        PacketPlayOutBlockChange blockChange = (PacketPlayOutBlockChange) packet;
        return blockChange.block.getBlock() instanceof BlockDiodeAbstract;
    }

    @Override
    public boolean isBlockChangePacket(Object packet)
    {
        return packet instanceof PacketPlayOutBlockChange;
    }
}
