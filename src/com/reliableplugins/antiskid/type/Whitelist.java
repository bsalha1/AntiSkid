/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.type;

import com.reliableplugins.antiskid.enums.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.TreeSet;
import java.util.UUID;

public class Whitelist
{
    private TreeSet<UUID> whitelisted = new TreeSet<>();
    private UUID creatorUUID;

    public Whitelist(Player player)
    {
        this.creatorUUID = player.getUniqueId();
        whitelisted.add(creatorUUID);
    }

    public Whitelist(UUID uuid)
    {
        this.creatorUUID = uuid;
        whitelisted.add(uuid);
    }

    public String getListString()
    {
        StringBuilder whitelistMsg = new StringBuilder();
        Player player;
        for(UUID id : whitelisted)
        {
            player = Bukkit.getPlayer(id);
            if(creatorUUID.equals(id)) continue;
            whitelistMsg.append(player.getName()).append(", ");
        }
        whitelistMsg = new StringBuilder(whitelistMsg.substring(0, whitelistMsg.lastIndexOf(", ")));
        return whitelistMsg.toString();
    }

    public int rawSize()
    {
        return whitelisted.size();
    }

    public int size()
    {
        return whitelisted.size() - 1;
    }

    public boolean addPlayer(Player player)
    {
        return whitelisted.add(player.getUniqueId());
    }

    public boolean addPlayer(UUID uuid)
    {
        return whitelisted.add(uuid);
    }

    public boolean removePlayer(Player player)
    {
        return whitelisted.remove(player.getUniqueId());
    }

    public boolean removePlayer(UUID uuid)
    {
        return whitelisted.remove(uuid);
    }

    public boolean containsPlayer(Player player)
    {
        return whitelisted.contains(player.getUniqueId());
    }

    public boolean containsPlayer(UUID uuid)
    {
        return whitelisted.contains(uuid);
    }

    public TreeSet<UUID> getUUIDs()
    {
        return this.whitelisted;
    }
}
