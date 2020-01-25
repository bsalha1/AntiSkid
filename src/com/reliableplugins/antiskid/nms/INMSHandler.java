/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.nms;

import com.reliableplugins.antiskid.type.Vector;
import com.reliableplugins.antiskid.type.packet.Packet;
import com.reliableplugins.antiskid.type.packet.PacketClientLeftClickBlock;
import com.reliableplugins.antiskid.type.packet.PacketServerBlockChange;
import com.reliableplugins.antiskid.type.packet.PacketServerMapChunkBulk;
import io.netty.channel.Channel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.TreeSet;
import java.util.UUID;

public interface INMSHandler
{
    String getVersion();

    Channel getSocketChannel(Player player);

    void sendBlockChangePacket(Player player, Material material, Location location);

    void broadcastBlockChangePacket(Material material, Location location, TreeSet<UUID> whitelist);

    Packet getPacket(Object packet);
}