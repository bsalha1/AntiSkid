/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.hook.FactionHook;
import com.reliableplugins.antiskid.hook.PlotSquared3Hook;
import com.reliableplugins.antiskid.hook.PlotSquared4Hook;
import com.reliableplugins.antiskid.hook.PlotSquaredHook;
import com.reliableplugins.antiskid.listeners.ListenFactionAction;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class MainConfig extends Config
{
    public boolean fastScan;

    public boolean factionsEnabled;
    public boolean whitelistFaction;
    private String minimumRank;
    public int minimumFactionRank;
    public List<World> factionsWorlds;

    public boolean plotsEnabled;
    public List<World> plotsWorlds;
    public PlotSquaredHook plotSquaredHook;

    public MainConfig()
    {
        super("config.yml");
    }

    @Override
    public void load()
    {
        List<World> worlds = AntiSkid.INSTANCE.getServer().getWorlds();
        HashMap<String, World> worldNameMap = new HashMap<>();
        for(World world : worlds)
        {
            worldNameMap.put(world.getName(), world);
        }

        fastScan = getBoolean("fast-scan", false);

        // Factions Config
        factionsEnabled = getBoolean("factions.support", true);
        factionsWorlds = new ArrayList<>();
        if(factionsEnabled)
        {
            if(Bukkit.getPluginManager().isPluginEnabled("Factions"))
            {
                Bukkit.getPluginManager().registerEvents(new ListenFactionAction(), AntiSkid.INSTANCE);
                whitelistFaction = getBoolean("factions.whitelist-faction", true);
                minimumRank = getString("factions.minimum-rank", "moderator");
                minimumFactionRank = FactionHook.getRole(minimumRank);

                // Get Enabled Worlds
                List<String> factionsWorldsList = getStringList("factions.worlds", Collections.singletonList("factions"));
                for(String worldName : factionsWorldsList)
                {
                    if(!worldNameMap.containsKey(worldName))
                    {
                        Bukkit.getLogger().log(Level.WARNING, worldName + " is not a world!");
                    }
                    else
                    {
                        factionsWorlds.add(worldNameMap.get(worldName));
                    }
                }
            }
            else
            {
                Bukkit.getLogger().log(Level.SEVERE, "Factions jar not found");
            }
        }

        // PlotSquared Config
        plotsEnabled = getBoolean("plotsquared.support", true);
        plotsWorlds = new ArrayList<>();
        if(plotsEnabled)
        {
            if(Bukkit.getPluginManager().isPluginEnabled("PlotSquared"))
            {
                String plotSquaredVersion = Bukkit.getPluginManager().getPlugin("PlotSquared").getDescription().getVersion();
                if(plotSquaredVersion.startsWith("3"))
                {
                    plotSquaredHook = new PlotSquared3Hook();
                }
                else if(plotSquaredVersion.startsWith("4"))
                {
                    plotSquaredHook = new PlotSquared4Hook();
                }
                else
                {
                    Bukkit.getLogger().log(Level.SEVERE, "Unsupported PlotSquared version: " + plotSquaredVersion + " please contact developers!");
                    return;
                }

                // Get Enabled Worlds
                List<String> plotsWorldsList = getStringList("plotsquared.worlds", Collections.singletonList("plots"));
                for(String worldName : plotsWorldsList)
                {
                    if(!worldNameMap.containsKey(worldName))
                    {
                        Bukkit.getLogger().log(Level.WARNING, worldName + " is not a world!");
                    }
                    else
                    {
                        plotsWorlds.add(worldNameMap.get(worldName));
                    }
                }
            }
            else
            {
                Bukkit.getLogger().log(Level.SEVERE, "PlotSquared jar not found");
            }
        }

        save();
    }

    public List<String> getStringList(String key, List<String> def)
    {
        getConfig().addDefault(key, def);
        return getConfig().getStringList(key);
    }
}
