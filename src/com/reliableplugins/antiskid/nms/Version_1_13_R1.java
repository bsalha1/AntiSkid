/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.nms;

import com.reliableplugins.antiskid.type.packet.PacketClientLeftClickBlock;
import com.reliableplugins.antiskid.type.packet.PacketServerBlockChange;
import com.reliableplugins.antiskid.type.packet.PacketServerExplosion;
import com.reliableplugins.antiskid.type.packet.PacketServerMapChunk;
import com.reliableplugins.antiskid.utils.Util;
import io.netty.channel.Channel;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.util.*;

public class Version_1_13_R1 extends ANMSHandler
{
    public Version_1_13_R1()
    {
        packetWrapper = new HashMap<>();

        // PlayOutBlockChange
        packetWrapper.put(PacketPlayOutBlockChange.class, pair ->
        {
            try
            {
                PacketPlayOutBlockChange blockChange = (PacketPlayOutBlockChange) pair.getKey();
                BlockPosition bpos;
                bpos = Util.getPrivateField("a", blockChange);

                return new PacketServerBlockChange(new Location(pair.getValue().getWorld(), bpos.getX(), bpos.getY(), bpos.getZ()), CraftMagicNumbers.getMaterial(blockChange.block.getBlock()));
            }
            catch(Exception e)
            {
                return null;
            }
        });

        // PlayOutMapChunk
        packetWrapper.put(PacketPlayOutMapChunk.class, pair ->
        {
            try
            {
                PacketPlayOutMapChunk mapChunk = (PacketPlayOutMapChunk) pair.getKey();
                int x = Util.getPrivateField("a", mapChunk);
                int z = Util.getPrivateField("b", mapChunk);
                return new PacketServerMapChunk(pair.getValue().getWorld().getChunkAt(x, z));
            }
            catch(Exception e)
            {
                return null;
            }
        });

        // PlayOutExplosion
        packetWrapper.put(PacketPlayOutExplosion.class, pair ->
        {
            try
            {
                PacketPlayOutExplosion pack = (PacketPlayOutExplosion) pair.getKey();
                List<BlockPosition> bposes = Util.getPrivateField("e", pack);
                Set<Location> positions = new HashSet<>();
                for(BlockPosition bpos : bposes)
                {
                    positions.add(new Location(pair.getValue().getWorld(), bpos.getX(), bpos.getY(), bpos.getZ()));
                }
                return new PacketServerExplosion(positions);
            }
            catch(Exception e)
            {
                return null;
            }
        });

        // PlayInBlockDig
        packetWrapper.put(PacketPlayInBlockDig.class, pair ->
        {
            try
            {
                PacketPlayInBlockDig pack = (PacketPlayInBlockDig) pair.getKey();
                BlockPosition bpos = pack.b();
                return new PacketClientLeftClickBlock(new Location(pair.getValue().getWorld(), bpos.getX(), bpos.getY(), bpos.getZ()));
            }
            catch(Exception e)
            {
                return null;
            }
        });
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
}

