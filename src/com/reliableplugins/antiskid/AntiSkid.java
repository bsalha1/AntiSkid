/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid;

import com.reliableplugins.antiskid.commands.*;
import com.reliableplugins.antiskid.config.MainConfig;
import com.reliableplugins.antiskid.config.MessageConfig;
import com.reliableplugins.antiskid.listeners.*;
import com.reliableplugins.antiskid.nms.*;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.utils.Cache;
import com.reliableplugins.antiskid.utils.MessageManager;
import com.reliableplugins.antiskid.utils.ChannelManager;
import com.reliableplugins.antiskid.utils.Util;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

public class AntiSkid extends JavaPlugin implements Listener
{
    public volatile TreeMap<UUID, Map<Chunk, Set<Location>>> diodes = new TreeMap<>();
    public volatile TreeMap<UUID, Whitelist> whitelists = new TreeMap<>();
    public volatile TreeMap<UUID, Pair<Location, Location>> selectionPoints = new TreeMap<>();
    public volatile Cache cache;

    public volatile Semaphore lock;
    private boolean isFactions;
    private boolean isPlots;

    private ANMSHandler nmsHandler;
    private CommandHandler cmdHandler;

    private PluginManager pluginManager;
    private ChannelManager listenerManager;
    private MessageManager messageManager;

    private String minimumFactionRank = null;
    private Set<World> factionsWorlds = new HashSet<>();
    private Set<World> plotsWorlds = new HashSet<>();

    private MessageConfig messageConfig;
    private MainConfig mainConfig;

    @Override
    public void onEnable()
    {
        lock = new Semaphore(1);
        pluginManager = Bukkit.getPluginManager();

        nmsHandler = getNMSHandler();
        cmdHandler = new CommandHandler(this);
        cmdHandler.addCommand(new CommandOn());
        cmdHandler.addCommand(new CommandTool());
        cmdHandler.addCommand(new CommandOff());
        cmdHandler.addCommand(new CommandWhitelist());
        cmdHandler.addCommand(new CommandReload());
        cmdHandler.addCommand(new CommandClear());


        loadConfigs();

        listenerManager = new ChannelManager(this);
        pluginManager.registerEvents(new ListenPlayerLoginLogout(this), this);
        pluginManager.registerEvents(new ListenDiodeAction(this), this);
        pluginManager.registerEvents(new ListenUseSelectionTool(this), this);
        listenerManager.loadChannelListener(new ChannelListener(this));
        cache = new Cache(this);

        this.getLogger().log(Level.INFO, "AntiSkid v1.0 has been loaded");
    }

    public void loadConfigs()
    {
        factionsWorlds.clear();
        plotsWorlds.clear();

        mainConfig = new MainConfig(this, "config.yml");
        messageConfig = new MessageConfig(this, "messages.yml");

        // Check if Factions support is enabled
        isFactions = mainConfig.getFileConfiguration().getBoolean("factions.support");
        if(isFactions)
        {
            if(pluginManager.isPluginEnabled("Factions"))
            {
                // Get worlds
                List<String> factionsWorldNames = mainConfig.getFileConfiguration().getStringList("factions.worlds");
                for(String s : factionsWorldNames)
                {
                    this.getServer().getWorlds().forEach((world) ->
                    {
                        if(world.getName().equalsIgnoreCase(s))
                        {
                            factionsWorlds.add(world);
                        }
                    });
                }

                minimumFactionRank = mainConfig.getFileConfiguration().getString("factions.minimum-rank");
                pluginManager.registerEvents(new ListenUnclaim(this), this);
                this.getLogger().log(Level.INFO, "Factions support enabled!");
            }
            else
            {
                isFactions = false;
                this.getLogger().log(Level.SEVERE, "Factions jar was not found!");
            }
        }

        // Check if PlotSquared support is enabled
        isPlots = mainConfig.getFileConfiguration().getBoolean("plotsquared.support");
        if(isPlots)
        {
            if(pluginManager.isPluginEnabled("PlotSquared"))
            {
                // Get worlds
                List<String> plotsWorldNames = mainConfig.getFileConfiguration().getStringList("plotsquared.worlds");
                for(String s : plotsWorldNames)
                {
                    this.getServer().getWorlds().forEach((world) ->
                    {
                        if(world.getName().equalsIgnoreCase(s))
                        {
                            plotsWorlds.add(world);
                        }
                    });
                }
                this.getLogger().log(Level.INFO, "PlotSquared support enabled!");
            }
            else
            {
                isPlots = false;
                this.getLogger().log(Level.SEVERE, "PlotSquared jar was not found!");
            }
        }

        messageManager = new MessageManager(messageConfig);
    }

    private ANMSHandler getNMSHandler()
    {
        switch(getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3])
        {
            case "v1_8_R2":
                return new Version_1_8_R2();
            case "v1_8_R3":
                return new Version_1_8_R3();
            case "v_1_9_R1":
                return new Version_1_9_R1();
            case "v_1_9_R2":
                return new Version_1_9_R2();
            case "v_1_10_R1":
                return new Version_1_10_R1();
            case "v1_11_R1":
                return new Version_1_11_R1();
            case "v1_12_R1":
                return new Version_1_12_R1();
            case "v1_13_R1":
                return new Version_1_13_R1();
            case "v1_13_R2":
                return new Version_1_13_R2();
            case "v1_14_R1":
                return new Version_1_14_R1();
            case "v1_15_R1":
            default:
                return new Version_1_15_R1();
        }
    }

    @Override
    public void onDisable()
    {
        this.getLogger().log(Level.INFO, "Cleanup: Start cleanup");
        for(Map<Chunk, Set<Location>> chunksMap : diodes.values())
        {
            for(Chunk chunk : chunksMap.keySet())
            {
                Util.reloadChunk(chunk);
            }
        }
        this.getLogger().log(Level.INFO, "Cleanup: All protected repeaters have been revealed");

        this.listenerManager.unloadChannelListener();
        this.getLogger().log(Level.INFO, "Cleanup: All packet listeners have been removed");

        this.messageConfig.save();
        this.mainConfig.save();

        this.getLogger().log(Level.INFO, "AntiSkid has been unloaded");
    }

    public boolean isFactions()
    {
        return isFactions;
    }

    public MainConfig getMainConfig()
    {
        return mainConfig;
    }

    public MessageConfig getMessageConfig()
    {
        return messageConfig;
    }

    public ChannelManager getPacketManager()
    {
        return listenerManager;
    }

    public MessageManager getMessageManager()
    {
        return this.messageManager;
    }

    public ANMSHandler getNMS()
    {
        return nmsHandler;
    }

    public boolean isPlots()
    {
        return isPlots;
    }

    public Set<World> getFactionsWorlds()
    {
        return factionsWorlds;
    }

    public Set<World> getPlotsWorlds()
    {
        return plotsWorlds;
    }
}
