/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */
package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.config.Message;
import com.reliableplugins.antiskid.nms.ANMSHandler;
import com.reliableplugins.antiskid.task.ATask;
import com.reliableplugins.antiskid.type.packet.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;
import java.util.Set;

@ChannelHandler.Sharable
public class ChannelListener extends AChannelListener
{
    // Sending data from server to client
    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception
    {
        ANMSHandler nmsHandler = AntiSkid.INSTANCE.getNMS();
        Packet wrappedPacket = nmsHandler.getPacket(packet, player);

        // MAP CHUNK PACKET
        if(wrappedPacket instanceof PacketServerMapChunk)
        {
            PacketServerMapChunk pack = (PacketServerMapChunk) wrappedPacket;
            Chunk chunk = pack.getChunk();

            AntiSkid.INSTANCE.startSynchronousTask(() ->
            {
                protectChunk(chunk);
            });
        }



        // MAP CHUNK BULK PACKET
        else if(wrappedPacket instanceof PacketServerMapChunkBulk)
        {
            PacketServerMapChunkBulk pack = (PacketServerMapChunkBulk) wrappedPacket;
            Chunk[] chunks = pack.getChunks();

            AntiSkid.INSTANCE.startSynchronousTask(()->
            {
                for(Chunk chunk : chunks)
                {
                    protectChunk(chunk);
                }
            });
        }



        // BLOCK CHANGE PACKET
        else if(wrappedPacket instanceof PacketServerBlockChange)
        {
            PacketServerBlockChange pack = (PacketServerBlockChange) wrappedPacket;
            Material material = pack.getMaterial();

            // If is not a diode blockchange continue transmission
            if(!material.equals(Material.DIODE_BLOCK_OFF) && !material.equals(Material.DIODE_BLOCK_ON) && !material.equals(Material.DIODE))
            {
                super.write(context, packet, promise);
                return;
            }

            Location location = pack.getLocation();
            Chunk chunk = location.getChunk();

            // Do not transmit diode blockchange (keep carpet)
            if(!AntiSkid.INSTANCE.cache.isWhitelisted(player, chunk))
            {
                return;
            }

            // If diode isn't already protected, add it
            Map<Chunk, Set<Location>> diodes = AntiSkid.INSTANCE.diodes.get(player.getUniqueId());
            if(diodes != null && diodes.containsKey(chunk) && !diodes.get(chunk).contains(location))
            {
                AntiSkid.INSTANCE.startSynchronousTask(()->
                {
                    diodes.get(chunk).add(location);
                    AntiSkid.INSTANCE.getNMS().broadcastBlockChangePacket(AntiSkid.INSTANCE.getReplacer(), location, AntiSkid.INSTANCE.cache.getWhitelist(chunk).getUUIDs());
                });
            }
        }

        else if(wrappedPacket instanceof PacketServerExplosion)
        {
            PacketServerExplosion pack = (PacketServerExplosion) wrappedPacket;

            AntiSkid.INSTANCE.startSynchronousTask(()->
            {
                for(Location location : pack.getLocations())
                {
                    if(AntiSkid.INSTANCE.cache.isProtected(location.getChunk(), location))
                    {
                        AntiSkid.INSTANCE.cache.unprotectLocation(location);
                    }
                }
            });
        }
        super.write(context, packet, promise);
    }

    private void protectChunk(Chunk chunk)
    {
        if(!AntiSkid.INSTANCE.cache.isWhitelisted(player, chunk))
        {
            for(Location location : AntiSkid.INSTANCE.cache.getLocations(chunk))
            {
                new ATask(1)
                {
                    @Override
                    public void run()
                    {
                        AntiSkid.INSTANCE.getNMS().sendBlockChangePacket(player, AntiSkid.INSTANCE.getReplacer(), location);
                    }
                };
            }
        }
    }

    // Simulates client sending data
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception
    {
        ANMSHandler nmsHandler = AntiSkid.INSTANCE.getNMS();
        Packet temp = nmsHandler.getPacket(packet, player);

        // CLIENTSIDE LEFT CLICK BLOCK
        if(temp instanceof PacketClientLeftClickBlock)
        {
            PacketClientLeftClickBlock pack = (PacketClientLeftClickBlock) temp;
            Location location = pack.getLocation();
            Material material = location.getBlock().getType();
            if(!(material.equals(Material.DIODE_BLOCK_OFF)
                    || material.equals(Material.DIODE_BLOCK_ON)
                    || material.equals(Material.DIODE)))
            {
                super.channelRead(channelHandlerContext, packet);
                return;
            }

            Chunk chunk = location.getChunk();
            if(!AntiSkid.INSTANCE.cache.isWhitelisted(player, chunk))
            {
                AntiSkid.INSTANCE.getNMS().sendBlockChangePacket(player, AntiSkid.INSTANCE.getReplacer(), location);
                player.sendMessage(Message.ERROR_PROTECTED_DIODE.getMessage());
                return;
            }
        }

        super.channelRead(channelHandlerContext, packet);
    }
}
