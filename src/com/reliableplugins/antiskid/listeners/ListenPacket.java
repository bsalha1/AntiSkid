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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.*;

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

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise)
    {
        INMSHandler nmsHandler = plugin.getNMS();
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
                    if(plugin.whitelists.get(entry.getKey()) != null
                            && plugin.whitelists.get(entry.getKey()).containsPlayer(player.getUniqueId()))
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

        if(nmsHandler.isBlockChangePacket(packet))
        {
            BlockChangePacket pack = nmsHandler.getBlockChangePacket(packet);
            if(pack == null)
            {
                exit(context, packet, promise);
                return;
            }
            if(!pack.getMaterial().equals(Material.DIODE_BLOCK_OFF) && !pack.getMaterial().equals(Material.DIODE_BLOCK_ON) && !pack.getMaterial().equals(Material.DIODE))
            {
                exit(context, packet, promise);
                return;
            }
            Vector packetLocation = pack.getLocation();

            World world = player.getWorld();
            Location location = new Location(world, packetLocation.getX(), packetLocation.getY(), packetLocation.getZ());
            try
            {
                plugin.lock.acquire();
            } catch(Exception ignored) { }
            for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
                if(entry.getValue().containsKey(world.getChunkAt(location)))
                {
                    if(plugin.whitelists.get(entry.getKey()).containsPlayer(player.getUniqueId())) continue;

                    new AbstractTask(plugin, 1)
                    {
                        @Override
                        public void run()
                        {
                            plugin.getNMS().sendBlockChangePacket(player, Material.CARPET, location);
                        }
                    };
                }
            plugin.lock.release();
        }

        exit(context, packet, promise);
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
