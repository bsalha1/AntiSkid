/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.hook.impl;

import com.massivecraft.factions.*;
import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.hook.PluginHook;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class FactionHook implements PluginHook<FactionHook>
{
    /**
     * Gets set of all adjacent chunks
     * @param chunk the chunk to start off at
     * @return set of all adjacent chunks
     */
    public static Set<Chunk> findChunkGroup(Player player, Chunk chunk)
    {
        Set<Chunk> group = new HashSet<>();
        World world = chunk.getWorld();
        String worldName = world.getName();
        FLocation fLoc = new FLocation(worldName, chunk.getX(), chunk.getZ());
        Faction playerFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
        Faction faction = Board.getInstance().getFactionAt(fLoc);

        // If player is in wilderness or not in their territory, return empty group
        if(faction.isWilderness() || !faction.equals(playerFaction))
        {
            return group;
        }

        /* CHECK POSITIVE X */
        for(int x = chunk.getX(); faction.equals(playerFaction); x++)
        {
            for(int z = chunk.getZ(); ; z++) // Check positive z
            {
                faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, z));
                if(!faction.equals(playerFaction)) break;
                group.add(world.getChunkAt(x, z));
            }

            for(int z = chunk.getZ() - 1; ; z--) // Check negative z
            {
                faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, z));
                if(!faction.equals(playerFaction)) break;
                group.add(world.getChunkAt(x, z));
            }
            faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, chunk.getZ()));
        }

        /* CHECK NEGATIVE X */
        faction = Board.getInstance().getFactionAt(new FLocation(worldName, chunk.getX() - 1, chunk.getZ()));
        for(int x = chunk.getX() - 1; faction.equals(playerFaction); x--)
        {
            for(int z = chunk.getZ(); ; z++) // Check positive z
            {
                faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, z));
                if(!faction.equals(playerFaction)) break;
                group.add(world.getChunkAt(x, z));
            }

            for(int z = chunk.getZ() - 1; ; z--) // Check negative z
            {
                faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, z));
                if(!faction.equals(playerFaction)) break;
                group.add(world.getChunkAt(x, z));
            }
            faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, chunk.getZ()));
        }
        return group;
    }

    public static boolean canBuild(Player player, Chunk chunk)
    {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        FLocation fLoc = new FLocation(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        Faction faction = Board.getInstance().getFactionAt(fLoc);

        if(faction.isWilderness())
        {
            return false;
        }

        if(faction.equals(fPlayer.getFaction()))
        {
            return true;
        }
        return false;
    }

    @Override
    public FactionHook setup(AntiSkid antiSkid)
    {
        return this;
    }

    @Override
    public String getName()
    {
        return "Factions";
    }

}
