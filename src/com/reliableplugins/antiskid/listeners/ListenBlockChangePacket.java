///*
// * Project: AntiSkid
// * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
// * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
// */
//
package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractTask;
import com.reliableplugins.antiskid.packets.RepeaterHidePacket;
import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/*
    block.a() = tile."blockname"
    block.C() = width
    block.E() = height
    block.G() = length
*/
public class ListenBlockChangePacket extends ChannelDuplexHandler
{
    private AntiSkid plugin;
    private Player player;

    public ListenBlockChangePacket(AntiSkid plugin, Player player)
    {
        this.plugin = plugin;
        this.player = player;
    }

    // Listen for repeater blockchange packets
    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise)
    {
        if(packet instanceof PacketPlayOutBlockChange)
        {
            PacketPlayOutBlockChange pack = (PacketPlayOutBlockChange) packet;

            // Ignore packet if it isn't a diode
            if(!(pack.block.getBlock() instanceof BlockRepeater))
            {
                exit(context, packet, promise);
                return;
            }

            BlockPosition bpos;
            try
            {
                Field field = PacketPlayOutBlockChange.class.getDeclaredField("a");
                field.setAccessible(true);
                bpos = (BlockPosition) field.get(pack);
            }
            catch(Exception e)
            {
                exit(context, packet, promise);
                e.printStackTrace();
                return;
            }

            World world = player.getWorld();
            Location location = new Location(world, bpos.getX(), bpos.getY(), bpos.getZ());

            // If the server-side block is a diode
            if(location.getBlock().getType().equals(Material.DIODE_BLOCK_OFF))
            {
                // For each protected diode
                for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
                {
                    // If packet block is a protected repeater
                    if(entry.getValue().containsKey(world.getChunkAt(location)))
                    {
                        // If player is whitelisted, do not mask repeaters
                        if(plugin.whitelists.get(entry.getKey()).contains(player.getUniqueId()))
                        {
                            continue;
                        }

                        // Send packet with 1 tick delay
                        new AbstractTask(plugin, 1)
                        {
                            @Override
                            public void run()
                            {
                                player.sendMessage("That repeater is protected.");
                                new RepeaterHidePacket(location).sendPacket(player);
                            }
                        };
                    }
                }
            }
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
