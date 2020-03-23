/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid;

import com.reliableplugins.antiskid.commands.*;
import com.reliableplugins.antiskid.config.MainConfig;
import com.reliableplugins.antiskid.config.MessageConfig;
import com.reliableplugins.antiskid.hook.FactionHook;
import com.reliableplugins.antiskid.listeners.*;
import com.reliableplugins.antiskid.nms.*;
import com.reliableplugins.antiskid.task.SyncTask;
import com.reliableplugins.antiskid.type.Pair;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.utils.Cache;
import com.reliableplugins.antiskid.utils.MessageManager;
import com.reliableplugins.antiskid.utils.ChannelManager;
import com.reliableplugins.antiskid.utils.Util;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

public class AntiSkid extends JavaPlugin implements Listener
{
    public volatile TreeMap<UUID, Map<Chunk, Set<Location>>> diodes = new TreeMap<>();
    public volatile TreeMap<UUID, Whitelist> whitelists = new TreeMap<>();
    public volatile TreeMap<UUID, Pair<Location, Location>> selectionPoints = new TreeMap<>();
    public volatile Cache cache;

    private volatile Semaphore lock;

    private ANMSHandler nmsHandler;
    private CommandHandler cmdHandler;

    private PluginManager pluginManager;
    private ChannelManager listenerManager;
    private MessageManager messageManager;

    private String version;
    private Material replacer;

    public Configurables config;
    private MessageConfig messageConfig;
    private MainConfig mainConfig;

    @Override
    public void onEnable()
    {
        version = "1.0";
        replacer = Material.REDSTONE_COMPARATOR_OFF;

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

        config = new Configurables();
        loadConfigs();
        try
        {
            InputStream initialStream = getResource("README.md");
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            File targetFile = new File(getDataFolder(), "README.md");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        listenerManager = new ChannelManager(this);
        pluginManager.registerEvents(new ListenPlayerLoginLogout(this), this);
        pluginManager.registerEvents(new ListenDiodeAction(this), this);
        pluginManager.registerEvents(new ListenUseSelectionTool(this), this);
        listenerManager.loadChannelListener(new ChannelListener(this));
        cache = new Cache(this);

        getLogger().log(Level.INFO, "AntiSkid v" + version + " has been loaded");
    }

    public void loadConfigs()
    {
        config.factionsWorlds = new HashSet<>();
        config.plotsWorlds = new HashSet<>();

        mainConfig = new MainConfig(this, "config.yml");
        messageConfig = new MessageConfig(this, "messages.yml");

        config.isFastScan = mainConfig.getFileConfiguration().getBoolean("fast-scan");

        // Check if Factions support is enabled
        config.isFactions = mainConfig.getFileConfiguration().getBoolean("factions.support");
        if(config.isFactions)
        {
            if(pluginManager.isPluginEnabled("Factions"))
            {
                // Get worlds
                List<String> factionsWorldNames = mainConfig.getFileConfiguration().getStringList("factions.worlds");
                for(String s : factionsWorldNames)
                {
                    getServer().getWorlds().forEach((world) ->
                    {
                        if(world.getName().equalsIgnoreCase(s))
                        {
                            config.factionsWorlds.add(world);
                        }
                    });
                }

                config.minimumFactionRank = FactionHook.getRole(mainConfig.getFileConfiguration().getString("factions.minimum-rank"));
                config.isFactionWhitelisted = mainConfig.getFileConfiguration().getBoolean("factions.whitelist-faction");
                pluginManager.registerEvents(new ListenFactionAction(this), this);
                getLogger().log(Level.INFO, "Factions support enabled!");
            }
            else
            {
                config.isFactions = false;
                getLogger().log(Level.SEVERE, "Factions jar was not found!");
            }
        }

        // Check if PlotSquared support is enabled
//        isPlots = mainConfig.getFileConfiguration().getBoolean("plotsquared.support");
//        if(isPlots)
//        {
//            if(pluginManager.isPluginEnabled("PlotSquared"))
//            {
//                // Get worlds
//                List<String> plotsWorldNames = mainConfig.getFileConfiguration().getStringList("plotsquared.worlds");
//                for(String s : plotsWorldNames)
//                {
//                    this.getServer().getWorlds().forEach((world) ->
//                    {
//                        if(world.getName().equalsIgnoreCase(s))
//                        {
//                            plotsWorlds.add(world);
//                        }
//                    });
//                }
//                this.getLogger().log(Level.INFO, "PlotSquared support enabled!");
//            }
//            else
//            {
//                isPlots = false;
//                this.getLogger().log(Level.SEVERE, "PlotSquared jar was not found!");
//            }
//        }

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
        getLogger().log(Level.INFO, "Cleanup: Start cleanup");
        for(Map<Chunk, Set<Location>> chunksMap : diodes.values())
        {
            for(Chunk chunk : chunksMap.keySet())
            {
                Util.reloadChunk(chunk);
            }
        }
        getLogger().log(Level.INFO, "Cleanup: All protected repeaters have been revealed");

        listenerManager.unloadChannelListener();
        getLogger().log(Level.INFO, "Cleanup: All packet listeners have been removed");

        messageConfig.save();
        mainConfig.save();

        getLogger().log(Level.INFO, "AntiSkid has been unloaded");
    }

    public void startSyncTask(SyncTask task)
    {
        try
        {
            lock.acquire();
        }
        catch(Exception ignored){}
        task.run();
        lock.release();
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
        return messageManager;
    }

    public ANMSHandler getNMS()
    {
        return nmsHandler;
    }

    public Material getReplacer()
    {
        return replacer;
    }

    public String getVersion()
    {
        return version;
    }

    public static class Configurables
    {
        public boolean isFastScan = false;

        public boolean isFactions = false;
        public int minimumFactionRank = 4;
        public boolean isFactionWhitelisted = true;
        public Set<World> factionsWorlds = new HashSet<>();

        public Set<World> plotsWorlds = new HashSet<>();
//    private boolean isPlots;
    }
}


