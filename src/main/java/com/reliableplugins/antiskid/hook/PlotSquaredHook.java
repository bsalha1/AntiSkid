package com.reliableplugins.antiskid.hook;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface PlotSquaredHook
{
    boolean isAdded(Player player, Location location);

    boolean isOwner(Player player, Location location);

    HashSet<Chunk> getChunks(Location location);

    Map<Chunk, Set<Location>> scanPlot(Location location);
}
