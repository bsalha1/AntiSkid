/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.nms.impl;

import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.type.Vector;
import com.reliableplugins.antiskid.type.packet.Packet;
import com.reliableplugins.antiskid.type.packet.PacketClientLeftClickBlock;
import com.reliableplugins.antiskid.type.packet.PacketServerBlockChange;
import com.reliableplugins.antiskid.type.packet.PacketServerMapChunkBulk;
import com.reliableplugins.antiskid.utils.Util;
import io.netty.channel.Channel;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_11_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;

public class Version_1_11_R1 implements INMSHandler
{
    @Override
    public String getVersion()
    {
        return "v1_11_R1";
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
    public Packet getPacket(Object packet)
    {
        if(packet instanceof PacketPlayOutBlockChange)
        {
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

            return new PacketServerBlockChange(new Vector(bpos.getX(), bpos.getY(), bpos.getZ()), CraftMagicNumbers.getMaterial(blockChange.block.getBlock()));
        }
//        else if(packet instanceof PacketPlayOutMapChunkBulk)
//        {
//            PacketPlayOutMapChunkBulk mapChunkBulk = (PacketPlayOutMapChunkBulk) packet;
//            try
//            {
//                int[] x = Util.getPrivateField("a", mapChunkBulk);
//                int[] z = Util.getPrivateField("b", mapChunkBulk);
//                World world = Util.getPrivateField("world", mapChunkBulk);
//                return new PacketServerMapChunkBulk(x, z, world.getWorld());
//            }
//            catch(Exception e)
//            {
//                return null;
//            }
//        }
        else if(packet instanceof PacketPlayInBlockDig)
            {
                PacketPlayInBlockDig pack = (PacketPlayInBlockDig) packet;
                BlockPosition bpos = pack.a();
                return new PacketClientLeftClickBlock(new Vector(bpos.getX(), bpos.getY(), bpos.getZ()));
            }

        return null;
    }
}
