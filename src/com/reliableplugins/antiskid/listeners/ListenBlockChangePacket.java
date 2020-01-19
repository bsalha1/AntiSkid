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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/*
    block.a() = tile."blockname"
    block.C() = width
    block.E() = height
    block.G() = length
*/
@ChannelHandler.Sharable
public class ListenBlockChangePacket extends PacketListener
{
    private AntiSkid plugin;

    public ListenBlockChangePacket(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise)
    {
        INMSHandler nmsHandler = plugin.getNMS();
        if(nmsHandler.isBlockChangePacket(packet) && nmsHandler.isDiodeBlockChangePacket(packet))
        {
            Vector packetLocation;
            try
            {
                packetLocation = nmsHandler.getLocation(packet);
            }
            catch(Exception e)
            {
                exit(context, packet, promise);
                return;
            }

            World world = player.getWorld();
            Location location = new Location(world, packetLocation.getX(), packetLocation.getY(), packetLocation.getZ());
            try
            {
                plugin.lock.acquire();
            }
            catch(Exception ignored) { }
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
