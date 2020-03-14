/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */
package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.task.AbstractTask;
import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.type.Vector;
import com.reliableplugins.antiskid.type.packet.*;
import com.reliableplugins.antiskid.utils.Util;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutExplosion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
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

    /**
     * Server-Side handler
     * @param context context
     * @param packet the packet being written onto
     * @param promise promise
     */
    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception
    {
        INMSHandler nmsHandler = plugin.getNMS();
        Packet temp = nmsHandler.getPacket(packet);

        // MAP CHUNK PACKET
        if(temp instanceof PacketServerMapChunk)
        {
            PacketServerMapChunk pack = (PacketServerMapChunk) temp;
            Chunk chunk = player.getWorld().getChunkAt(pack.getX(), pack.getZ());

            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) {}

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
                            plugin.getNMS().sendBlockChangePacket(player, Material.CARPET, location);
                        }
                    };
                }
            }

            plugin.lock.release();
        }



        // MAP CHUNK BULK PACKET
        else if(temp instanceof PacketServerMapChunkBulk)
        {
            PacketServerMapChunkBulk pack = (PacketServerMapChunkBulk) temp;
            Chunk[] chunks = pack.getChunks();

            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) {}

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
                                plugin.getNMS().sendBlockChangePacket(player, Material.CARPET, location);
                            }
                        };
                    }
                }
            }
            plugin.lock.release();
        }



        // BLOCK CHANGE PACKET
        else if(temp instanceof PacketServerBlockChange)
        {
            PacketServerBlockChange pack = (PacketServerBlockChange) temp;
            Material material = pack.getMaterial();

            // If is not a diode blockchange continue transmission
            if(!material.equals(Material.DIODE_BLOCK_OFF) && !material.equals(Material.DIODE_BLOCK_ON) && !material.equals(Material.DIODE))
            {
                super.write(context, packet, promise);
                return;
            }

            Vector<Integer> packetLocation = pack.getPosition();
            Location location = new Location(player.getWorld(), packetLocation.getX(), packetLocation.getY(), packetLocation.getZ());
            Chunk chunk = location.getChunk();

            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) {}

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
                plugin.getNMS().broadcastBlockChangePacket(Material.CARPET, location, plugin.cache.getWhitelist(chunk).getUUIDs());
            }
            plugin.lock.release();
        }

        else if(temp instanceof PacketServerExplosion)
        {
            PacketServerExplosion pack = (PacketServerExplosion) temp;
            for(Vector<Integer> position : pack.getPositions())
            {
//                plugin.cache.isProtected()
            }
        }
        super.write(context, packet, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception
    {
        INMSHandler nmsHandler = plugin.getNMS();
        Packet temp = nmsHandler.getPacket(packet);

        // CLIENTSIDE LEFT CLICK BLOCK
        if(temp instanceof PacketClientLeftClickBlock)
        {
            PacketClientLeftClickBlock pack = (PacketClientLeftClickBlock) temp;
            Vector<Integer> position = pack.getPosition();
            Location location = new Location(player.getWorld(), position.getX(), position.getY(), position.getZ());
            Material material = location.getBlock().getType();
            if(!(material.equals(Material.DIODE_BLOCK_OFF)
                    || material.equals(Material.DIODE_BLOCK_ON)
                    || material.equals(Material.DIODE)))
            {
                super.channelRead(channelHandlerContext, packet);
                return;
            }

            Chunk chunk = location.getChunk();
            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) {}

            // Cancel repeater reveal if not whitelisted
            if(!plugin.cache.isWhitelisted(player, chunk))
            {
                plugin.lock.release();
                plugin.getNMS().sendBlockChangePacket(player, Material.CARPET, location);
                player.sendMessage(plugin.getMessageManager().ERROR_PROTECTED_DIODE);
                return;
            }
            plugin.lock.release();
        }

        super.channelRead(channelHandlerContext, packet);
    }
}
