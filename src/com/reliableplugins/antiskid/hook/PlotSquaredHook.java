package com.reliableplugins.antiskid.hook;

import com.intellectualcrafters.plot.object.Plot;
import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlotSquaredHook implements PluginHook<PlotSquaredHook>
{
    public static boolean isAdded(Player player, Location location)
    {
        Plot plot = Plot.getPlot(new com.intellectualcrafters.plot.object.Location(
                location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getYaw(), location.getPitch()
        ));

        return plot != null && plot.isAdded(player.getUniqueId());
    }

    public static boolean isOwner(Player player, Location location)
    {
        Plot plot = Plot.getPlot(new com.intellectualcrafters.plot.object.Location(
                location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getYaw(), location.getPitch()
        ));

        return plot != null && plot.isOwner(player.getUniqueId());
    }

    @Override
    public PlotSquaredHook setup(AntiSkid antiSkid)
    {
        return this;
    }

    @Override
    public String getName()
    {
        return "PlotSquared";
    }
}
