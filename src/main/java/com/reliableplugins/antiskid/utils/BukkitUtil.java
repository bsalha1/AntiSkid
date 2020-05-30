package com.reliableplugins.antiskid.utils;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

public class BukkitUtil
{
    public static void reloadChunk(Chunk chunk)
    {
        chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
    }

    public static String color(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
