/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */
package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractTask;
import com.reliableplugins.antiskid.abstracts.PacketListener;
import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.type.Vector;
import com.reliableplugins.antiskid.type.packet.BlockChangePacket;
import com.reliableplugins.antiskid.type.packet.MapChunkBulkPacket;
import com.reliableplugins.antiskid.utils.Util;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ChannelHandler.Sharable
public class ListenPacket extends PacketListener
{
    private AntiSkid plugin;

    public ListenPacket(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Server-Side handler
     * @param context context
     * @param packet the packet being written onto
     * @param promise promise
     */
    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise)
    {
        INMSHandler nmsHandler = plugin.getNMS();

//        if(packet instanceof PacketPlayOutMultiBlockChange)
//        {
//            PacketPlayOutMultiBlockChange pack = (PacketPlayOutMultiBlockChange) packet;
//            try
//            {
//                PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[] bInfos = Util.getPrivateField("b", pack);
//                for(PacketPlayOutMultiBlockChange.MultiBlockChangeInfo b : bInfos)
//                {
//                    BlockPosition bpos = b.a();
//                    Bukkit.broadcastMessage("Short: " + b.b());
//
//                    Location location = new Location(player.getWorld(), bpos.getX(), bpos.getY(), bpos.getZ());
//                    Chunk chunk = location.getChunk();
//                    if(location.getBlock().getType().equals(Material.DIODE_BLOCK_OFF) || location.getBlock().getType().equals(Material.DIODE_BLOCK_ON))
//                    {
//
//                    }
//                }
//                PacketPlayOutMultiBlockChange toSend = new PacketPlayOutMultiBlockChange(clone.length, );
//
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
        /* MAP CHUNK BULK PACKET */
        if(nmsHandler.isMapChunkBulkPacket(packet))
        {
            MapChunkBulkPacket pack = nmsHandler.getMapChunkBulkPacket(packet);
            if(pack == null)
            {
                exit(context, packet, promise);
                return;
            }
            int[] chunksX = pack.getX();
            int[] chunksZ = pack.getZ();
            World world = pack.getWorld();

            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) { }
            for(int i = 0; i < chunksX.length; i++)
            {
                for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
                {
                    if(plugin.whitelists.get(entry.getKey()) != null && plugin.whitelists.get(entry.getKey()).containsPlayer(player.getUniqueId()))
                    {
                        continue;
                    }
                    for(Chunk chunk : entry.getValue().keySet())
                    {
                        if(chunk.getX() == chunksX[i] && chunk.getZ() == chunksZ[i] && world.equals(chunk.getWorld()))
                        {
                            for(Location location : entry.getValue().get(chunk))
                            {
                                new AbstractTask(plugin, 1)
                                {
                                    @Override
                                    public void run()
                                    {
                                        plugin.getNMS().sendBlockChangePacket(player, Material.CARPET, location);
                                    }
                                };
                            }
                        }
                    }
                }
            }
            plugin.lock.release();
        }



        /* BLOCK CHANGE PACKET */
        if(nmsHandler.isBlockChangePacket(packet))
        {
            BlockChangePacket pack = nmsHandler.getBlockChangePacket(packet);

            // If packet is null or is not a diode blockchange, return
            if(pack == null || (!pack.getMaterial().equals(Material.DIODE_BLOCK_OFF) && !pack.getMaterial().equals(Material.DIODE_BLOCK_ON) && !pack.getMaterial().equals(Material.DIODE)))
            {
                exit(context, packet, promise);
                return;
            }

            Vector packetLocation = pack.getLocation();
            Location location = new Location(player.getWorld(), packetLocation.getX(), packetLocation.getY(), packetLocation.getZ());
            Chunk chunk = location.getChunk();

            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) {}

            // If this chunk is protected and the player isn't whitelisted. Don't send blockchange
            if(plugin.cache.isProtected(chunk) && !plugin.cache.isWhitelisted(player, chunk))
            {
                plugin.lock.release();
                return;
            }
            plugin.lock.release();
        }
        exit(context, packet, promise);
    }

    /**
     * Client-Side handler
     * @param channelHandlerContext context of channel handler
     * @param packet the packet
     * @throws Exception -
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception
    {
        /* ON CLIENTSIDE RIGHT CLICK BLOCK */
        if(packet instanceof PacketPlayInBlockPlace)
        {
            PacketPlayInBlockPlace pack = (PacketPlayInBlockPlace) packet;
            BlockPosition bpos = pack.a();
            Location location = new Location(player.getWorld(), bpos.getX(), bpos.getY(), bpos.getZ());
            Material material = location.getBlock().getType();

            if(!(material.equals(Material.DIODE_BLOCK_OFF) || material.equals(Material.DIODE_BLOCK_ON) || material.equals(Material.DIODE)))
            {
                super.channelRead(channelHandlerContext, packet);
                return;
            }

            Chunk chunk = location.getChunk();
            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) {}

            // Do not update the block if the player isn't whitelisted to this protected chunk
            if(plugin.cache.isProtected(chunk) && !plugin.cache.isWhitelisted(player, chunk))
            {
                player.sendMessage(plugin.getMessageManager().ERROR_PROTECTED_DIODE);
                plugin.lock.release();
                return;
            }
            plugin.lock.release();
        }

        /* ON CLIENTSIDE LEFT CLICK BLOCK */
        if(packet instanceof PacketPlayInBlockDig)
        {
            PacketPlayInBlockDig pack = (PacketPlayInBlockDig) packet;
            BlockPosition bpos = pack.a();
            Location location = new Location(player.getWorld(), bpos.getX(), bpos.getY(), bpos.getZ());
            Material material = location.getBlock().getType();
            if(!(material.equals(Material.DIODE_BLOCK_OFF) || material.equals(Material.DIODE_BLOCK_ON) || material.equals(Material.DIODE)))
            {
                super.channelRead(channelHandlerContext, packet);
                return;
            }

            Chunk chunk = location.getChunk();
            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) {}

            // Send carpet if the player isn't whitelisted to this protected chunk
            if(plugin.cache.isProtected(chunk) && !plugin.cache.isWhitelisted(player, chunk))
            {
                plugin.getNMS().sendBlockChangePacket(player, Material.CARPET, location);
                plugin.lock.release();
                player.sendMessage(plugin.getMessageManager().ERROR_PROTECTED_DIODE);
                return;
            }
            plugin.lock.release();
        }

        super.channelRead(channelHandlerContext, packet);
    }

    private void exit(ChannelHandlerContext context, Object packet, ChannelPromise promise)
    {
        try
        {
            super.write(context, packet, promise);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
