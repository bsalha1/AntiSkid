/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.hook;

import com.massivecraft.factions.*;
import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class FactionHook
{
    @Nonnull
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
                if(!faction.equals(playerFaction))
                {
                    break;
                }
                group.add(world.getChunkAt(x, z));
            }

            for(int z = chunk.getZ() - 1; ; z--) // Check negative z
            {
                faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, z));
                if(!faction.equals(playerFaction))
                {
                    break;
                }
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
                if(!faction.equals(playerFaction))
                {
                    break;
                }
                group.add(world.getChunkAt(x, z));
            }

            for(int z = chunk.getZ() - 1; ; z--) // Check negative z
            {
                faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, z));
                if(!faction.equals(playerFaction))
                {
                    break;
                }
                group.add(world.getChunkAt(x, z));
            }
            faction = Board.getInstance().getFactionAt(new FLocation(worldName, x, chunk.getZ()));
        }
        return group;
    }

    public static int getRole(String role)
    {
        if(role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("leader"))
        {
            return 4;
        }
        else if(role.equalsIgnoreCase("coleader"))
        {
            return 3;
        }
        else if(role.equalsIgnoreCase("moderator") || role.equalsIgnoreCase("mod"))
        {
            return 2;
        }
        else if(role.equalsIgnoreCase("normal") || role.equalsIgnoreCase("member"))
        {
            return 1;
        }
        else if(role.equalsIgnoreCase("recruit"))
        {
            return 0;
        }
        else
        {
            return 5;
        }
    }

    public static int getRole(Player player)
    {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if(fPlayer != null)
        {
            switch(fPlayer.getRole())
            {
                case ADMIN:
                    return 4;
                case COLEADER:
                    return 3;
                case MODERATOR:
                    return 2;
                case NORMAL:
                    return 1;
                case RECRUIT:
                default:
                    return 0;
            }
        }
        else
        {
            return -1;
        }
    }

    @Nonnull
    public static String getRoleName(int roleId)
    {
        switch(roleId)
        {
            case 4:
                return "admin";
            case 3:
                return "coleader";
            case 2:
                return "moderator";
            case 1:
                return "member";
            case 0:
                return "recruit";
            default:
                return "unknown";
        }
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

        return faction.equals(fPlayer.getFaction());
    }

    @Nonnull
    public static HashSet<Player> getFactionMembers(Player player)
    {
        HashSet<Player> members = new HashSet<>();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if(fPlayer != null)
        {
            Faction faction = fPlayer.getFaction();
            if(faction != null)
            {
                for(FPlayer fp : fPlayer.getFaction().getFPlayers())
                {
                    members.add(fp.getPlayer());
                }
            }
        }
        return members;
    }
}
