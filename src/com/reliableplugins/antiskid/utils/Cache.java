package com.reliableplugins.antiskid.utils;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Cache
{
    private AntiSkid plugin;
    public Cache(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    public boolean ownsChunk(Player player, Chunk chunk)
    {
        if(!plugin.diodes.containsKey(player.getUniqueId()))
        {
            return false;
        }
        return plugin.diodes.get(player.getUniqueId()).containsKey(chunk);
    }

    public boolean isWhitelisted(Player player, Chunk chunk)
    {
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
        {
            if(entry.getValue().containsKey(chunk))
            {
                return plugin.whitelists.get(entry.getKey()).containsPlayer(player);
            }
        }
        return true;
    }

    public boolean isPlayerProtected(Player player)
    {
        return plugin.diodes.containsKey(player.getUniqueId());
    }

    public boolean isProtected(Chunk chunk)
    {
        for(Map<Chunk, Set<Location>> map : plugin.diodes.values())
        {
            if(map.containsKey(chunk))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isProtected(Chunk chunk, Location location)
    {
        for(Map<Chunk, Set<Location>> map : plugin.diodes.values())
        {
            if(map.containsKey(chunk))
            {
                return map.get(chunk).contains(location);
            }
        }
        return false;
    }

    public boolean isProtected(Chunk chunk, Location location, Player player)
    {
        Map<Chunk, Set<Location>> map = plugin.diodes.get(player.getUniqueId());
        if(map == null)
        {
            return false;
        }

        if(map.containsKey(chunk))
        {
            return map.get(chunk).contains(location);
        }
        return false;
    }

    public Set<Location> getLocations(Chunk chunk)
    {
        for(Map<Chunk, Set<Location>> map : plugin.diodes.values())
        {
            if(map.containsKey(chunk))
            {
                return map.get(chunk);
            }
        }
        return null;
    }
}
