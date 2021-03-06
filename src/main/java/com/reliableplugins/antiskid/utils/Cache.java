package com.reliableplugins.antiskid.utils;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.type.Whitelist;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Cache
{
    public boolean ownsChunk(Player player, Chunk chunk)
    {
        if(!AntiSkid.INSTANCE.diodes.containsKey(player.getUniqueId()))
        {
            return false;
        }
        return AntiSkid.INSTANCE.diodes.get(player.getUniqueId()).containsKey(chunk);
    }

    public Whitelist getWhitelist(Chunk chunk)
    {
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : AntiSkid.INSTANCE.diodes.entrySet())
        {
            if(entry.getValue().containsKey(chunk))
            {
                return AntiSkid.INSTANCE.whitelists.get(entry.getKey());
            }
        }

        return null;
    }

    public boolean isWhitelisted(Player player, Chunk chunk)
    {
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : AntiSkid.INSTANCE.diodes.entrySet())
        {
            if(entry.getValue().containsKey(chunk))
            {
                return AntiSkid.INSTANCE.whitelists.get(entry.getKey()).containsPlayer(player);
            }
        }
        return true;
    }

    public void unprotectChunk(Chunk chunk)
    {
        for(Map<Chunk, Set<Location>> entry : AntiSkid.INSTANCE.diodes.values())
        {
            entry.remove(chunk);
        }
    }

    public void unprotectLocation(Location location)
    {
        for(Map<Chunk, Set<Location>> map : AntiSkid.INSTANCE.diodes.values())
        {
            if(map.containsKey(location.getChunk()))
            {
                map.get(location.getChunk()).remove(location);
                return;
            }
        }
    }

    public boolean isPlayerProtected(Player player)
    {
        return AntiSkid.INSTANCE.diodes.containsKey(player.getUniqueId());
    }

    public boolean isProtected(Chunk chunk)
    {
        for(Map<Chunk, Set<Location>> map : AntiSkid.INSTANCE.diodes.values())
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
        for(Map<Chunk, Set<Location>> map : AntiSkid.INSTANCE.diodes.values())
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
        Map<Chunk, Set<Location>> map = AntiSkid.INSTANCE.diodes.get(player.getUniqueId());
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
        for(Map<Chunk, Set<Location>> map : AntiSkid.INSTANCE.diodes.values())
        {
            if(map.containsKey(chunk))
            {
                return map.get(chunk);
            }
        }
        return null;
    }
}
