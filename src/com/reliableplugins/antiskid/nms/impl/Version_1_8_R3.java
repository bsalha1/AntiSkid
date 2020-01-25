/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.nms.impl;

import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.type.Vector;
import com.reliableplugins.antiskid.type.packet.BlockChangePacket;
import com.reliableplugins.antiskid.type.packet.MapChunkBulkPacket;
import com.reliableplugins.antiskid.utils.Util;
import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class Version_1_8_R3 implements INMSHandler
{
    @Override
    public String getVersion()
    {
        return "v1_8_R3";
    }

    @Override
    public Channel getSocketChannel(Player player)
    {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
    }

    @Override
    public void sendBlockChangePacket(Player player, Material material, Location location)
    {
        PacketPlayOutBlockChange packet =  new PacketPlayOutBlockChange(
                ((CraftWorld) location.getWorld()).getHandle(),
                new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));

        packet.block = CraftMagicNumbers.getBlock(material).getBlockData();

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
    public MapChunkBulkPacket getMapChunkBulkPacket(Object packet)
    {
        if(!(packet instanceof PacketPlayOutMapChunkBulk))
        {
            return null;
        }

        PacketPlayOutMapChunkBulk mapChunkBulk = (PacketPlayOutMapChunkBulk) packet;
        try
        {
            int[] x = Util.getPrivateField("a", mapChunkBulk);
            int[] z = Util.getPrivateField("b", mapChunkBulk);
            World world = Util.getPrivateField("world", mapChunkBulk);
            return new MapChunkBulkPacket(x, z, world.getWorld());
        }
        catch(Exception e)
        {
            return null;
        }

    }

    @Override
    public BlockChangePacket getBlockChangePacket(Object packet)
    {
        if(!(packet instanceof PacketPlayOutBlockChange))
        {
            return null;
        }

        PacketPlayOutBlockChange blockChange = (PacketPlayOutBlockChange) packet;
        BlockPosition bpos;
        try
        {
            bpos = Util.getPrivateField("a", blockChange);
        }
        catch(Exception e)
        {
            return null;
        }

        return new BlockChangePacket(
                new Vector(bpos.getX(), bpos.getY(), bpos.getZ()),
                CraftMagicNumbers.getMaterial(blockChange.block.getBlock()));
    }

    @Override
    public Vector getLocation(Object packet)
    {
        if(!(packet instanceof PacketPlayOutBlockChange))
        {
            return null;
        }

        PacketPlayOutBlockChange blockChange = (PacketPlayOutBlockChange) packet;
        BlockPosition bpos;
        try
        {
            bpos = Util.getPrivateField("a", blockChange);
        }
        catch(Exception e)
        {
            return null;
        }

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

    @Override
    public boolean isMapChunkBulkPacket(Object packet)
    {
        return packet instanceof PacketPlayOutMapChunkBulk;
    }

    @Override
    public int[][] getChunkCoordinates(Object packet)
    {
        if(!(packet instanceof PacketPlayOutMapChunkBulk))
        {
            return null;
        }

        HashMap<Integer, Integer> chunkCoords = new HashMap<>();
        PacketPlayOutMapChunkBulk mapChunkBulk = (PacketPlayOutMapChunkBulk) packet;
        int[][] coords;
        try
        {
            int[] x = Util.getPrivateField("a", mapChunkBulk);
            int[] y = Util.getPrivateField("b", mapChunkBulk);
            coords = new int[][]{x, y};
        }
        catch(Exception e)
        {
            return null;
        }

        return coords;
    }
}
