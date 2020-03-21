//package com.reliableplugins.antiskid.hook;
//
//import com.intellectualcrafters.plot.object.Plot;
//import com.massivecraft.factions.P;
//import com.reliableplugins.antiskid.AntiSkid;
//import org.bukkit.*;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
//
//import java.util.*;
//
//public class PlotSquaredHook implements PluginHook<PlotSquaredHook>
//{
//    public static boolean isAdded(Player player, Location location)
//    {
//        Plot plot = Plot.getPlot(new com.intellectualcrafters.plot.object.Location(
//                location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getYaw(), location.getPitch()
//        ));
//
//        return plot != null && plot.isAdded(player.getUniqueId());
//    }
//
//    public static boolean isOwner(Player player, Location location)
//    {
//        Plot plot = Plot.getPlot(new com.intellectualcrafters.plot.object.Location(
//                location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getYaw(), location.getPitch()
//        ));
//
//        return plot != null && plot.isOwner(player.getUniqueId());
//    }
//
//    public static HashSet<Chunk> getChunks(Location location)
//    {
//        World world = location.getWorld();
//        HashSet<Chunk> chunks = new HashSet<>();
//        Plot plot = Plot.getPlot(new com.intellectualcrafters.plot.object.Location(
//                location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getYaw(), location.getPitch()
//        ));
//
//        if(plot == null)
//        {
//            return chunks;
//        }
//
//        List<com.intellectualcrafters.plot.object.Location> corners = plot.getAllCorners();
//        int minX = corners.get(0).getX();
//        int maxX = corners.get(2).getX();
//
//        int minZ = corners.get(0).getZ();
//        int maxZ = corners.get(1).getZ();
//
//        for(int x = minX; x < maxX; x++)
//            for(int z = minZ; z < maxZ; z++)
//            {
//                chunks.add(world.getChunkAt(x >> 4, z >> 4));
//            }
//        return chunks;
//    }
//
//    public static Map<Chunk, Set<Location>> scanPlot(Location location)
//    {
//        Map<Chunk, Set<Location>> diodes = new HashMap<>();
//
//        World world = location.getWorld();
//        Plot plot = Plot.getPlot(new com.intellectualcrafters.plot.object.Location(
//                location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getYaw(), location.getPitch()
//        ));
//        List<com.intellectualcrafters.plot.object.Location> corners = plot.getAllCorners();
//        int minX = corners.get(0).getX();
//        int maxX = corners.get(2).getX();
//
//        int minZ = corners.get(0).getZ();
//        int maxZ = corners.get(1).getZ();
//
//        for(int x = minX; x < maxX; x++)
//            for(int z = minZ; z < maxZ; z++)
//                for(int y = 0; y < 256; y++)
//                {
//                    Block block = world.getBlockAt(x,y,z);
//                    if(block.getType().equals(Material.DIODE_BLOCK_OFF))
//                    {
//                        if(!diodes.containsKey(block.getChunk()))
//                        {
//                            diodes.put(block.getChunk(), new HashSet<>());
//                        }
//                        diodes.get(block.getChunk()).add(block.getLocation());
//                    }
//                }
//        return diodes;
//    }
//
//    @Override
//    public PlotSquaredHook setup(AntiSkid antiSkid)
//    {
//        return this;
//    }
//
//    @Override
//    public String getName()
//    {
//        return "PlotSquared";
//    }
//}
