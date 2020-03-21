/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */
package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.nms.ANMSHandler;
import com.reliableplugins.antiskid.task.AbstractTask;
import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.type.Vector;
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
    private AntiSkid plugin;

    public ChannelListener(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    // Sending data from server to client
    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception
    {
        ANMSHandler nmsHandler = plugin.getNMS();
        Packet wrappedPacket = nmsHandler.getPacket(packet, player);

        // MAP CHUNK PACKET
        if(wrappedPacket instanceof PacketServerMapChunk)
        {
            PacketServerMapChunk pack = (PacketServerMapChunk) wrappedPacket;
            Chunk chunk = pack.getChunk();

            try{ plugin.lock.acquire(); } catch(Exception ignored){}

            // If player not whitelisted, send carpet instead of diode
            if(!plugin.cache.isWhitelisted(player, chunk))
            {
                for(Location location : plugin.cache.getLocations(chunk))
                {
                    new AbstractTask(plugin, 1)
                    {
                        @Override
                        public void run()
                        {
                            plugin.getNMS().sendBlockChangePacket(player, plugin.getReplacer(), location);
                        }
                    };
                }
            }

            plugin.lock.release();
        }



        // MAP CHUNK BULK PACKET
        else if(wrappedPacket instanceof PacketServerMapChunkBulk)
        {
            PacketServerMapChunkBulk pack = (PacketServerMapChunkBulk) wrappedPacket;
            Chunk[] chunks = pack.getChunks();

            try{ plugin.lock.acquire(); } catch(Exception ignored){}

            for(Chunk chunk : chunks)
            {
                // If player not whitelisted, send carpet instead of diode
                if(!plugin.cache.isWhitelisted(player, chunk))
                {
                    for(Location location : plugin.cache.getLocations(chunk))
                    {
                        new AbstractTask(plugin, 1)
                        {
                            @Override
                            public void run()
                            {
                                plugin.getNMS().sendBlockChangePacket(player, plugin.getReplacer(), location);
                            }
                        };
                    }
                }
            }
            plugin.lock.release();
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

            try { plugin.lock.acquire(); } catch(Exception ignored){}

            // Do not transmit diode blockchange (keep carpet)
            if(!plugin.cache.isWhitelisted(player, chunk))
            {
                plugin.lock.release();
                return;
            }

            // If diode isn't already protected, add it
            Map<Chunk, Set<Location>> diodes = plugin.diodes.get(player.getUniqueId());
            if(diodes != null && diodes.containsKey(chunk) && !diodes.get(chunk).contains(location))
            {
                diodes.get(chunk).add(location);
                plugin.getNMS().broadcastBlockChangePacket(plugin.getReplacer(), location, plugin.cache.getWhitelist(chunk).getUUIDs());
            }
            plugin.lock.release();
        }

        else if(wrappedPacket instanceof PacketServerExplosion)
        {
            PacketServerExplosion pack = (PacketServerExplosion) wrappedPacket;
            try{ plugin.lock.acquire(); } catch(Exception ignored){}

            for(Location location : pack.getLocations())
            {
                if(plugin.cache.isProtected(location.getChunk(), location))
                {
                    plugin.cache.unprotectLocation(location);
                }
            }
            plugin.lock.release();
        }
        super.write(context, packet, promise);
    }

    // Simulates client sending data
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception
    {
        ANMSHandler nmsHandler = plugin.getNMS();
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
            try { plugin.lock.acquire(); } catch(Exception ignored) {}

            // Cancel repeater reveal if not whitelisted
            if(!plugin.cache.isWhitelisted(player, chunk))
            {
                plugin.lock.release();
                plugin.getNMS().sendBlockChangePacket(player, plugin.getReplacer(), location);
                player.sendMessage(plugin.getMessageManager().ERROR_PROTECTED_DIODE);
                return;
            }
            plugin.lock.release();
        }

        super.channelRead(channelHandlerContext, packet);
    }
}
