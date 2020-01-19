package com.reliableplugins.antiskid.nms;

import com.reliableplugins.antiskid.type.Vector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

public interface INMSHandler
{
    String getVersion();

    void sendBlockChangePacket(Player player, Material material, Location location);

    void broadcastBlockChangePacket(Material material, Location location, TreeSet<UUID> whitelist);

    boolean isDiodeBlockChangePacket(Object packet);

    boolean isBlockChangePacket(Object packet);

    Vector getLocation(Object packet) throws IllegalAccessException, NoSuchFieldException;
}