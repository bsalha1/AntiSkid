/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid;

import com.reliableplugins.antiskid.commands.Base_CommandAntiSkid;
import com.reliableplugins.antiskid.config.MainConfig;
import com.reliableplugins.antiskid.config.MessageConfig;
import com.reliableplugins.antiskid.listeners.*;
import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.nms.NMSManager;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.utils.Cache;
import com.reliableplugins.antiskid.utils.MessageManager;
import com.reliableplugins.antiskid.utils.PacketManager;
import com.reliableplugins.antiskid.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

public class AntiSkid extends JavaPlugin
{
    public volatile TreeMap<UUID, Map<Chunk, Set<Location>>> diodes = new TreeMap<>();
    public volatile TreeMap<UUID, Whitelist> whitelists = new TreeMap<>();
    public Cache cache;

    public volatile Semaphore lock;
    private boolean isFactions;
    private boolean isPlots;

    private INMSHandler nmsHandler;
    private NMSManager nmsManager;
    private PluginManager plugMan;
    private PacketManager packMan;
    private MessageManager messageManager;

    private String minimumFactionRank = null;

    private MessageConfig messageConfig;
    private MainConfig mainConfig;

    @Override
    public void onEnable()
    {
        lock = new Semaphore(1);
        plugMan = Bukkit.getPluginManager();
        packMan = new PacketManager(this);
        nmsManager = new NMSManager(this);
        loadConfigs();

        new Base_CommandAntiSkid(this);

        plugMan.registerEvents(new ListenPlayerJoin(this), this);
        plugMan.registerEvents(new ListenDiodeAction(this), this);
        packMan.loadPacketListener(new ListenPacket(this));

        cache = new Cache(this);
        this.getLogger().log(Level.INFO, "AntiSkid v1.0 has been loaded");
    }

    public void loadConfigs()
    {
        mainConfig = new MainConfig(this, "config.yml");
        messageConfig = new MessageConfig(this, "messages.yml");

        if(mainConfig.isNew())
        {
            this.getLogger().log(Level.INFO, mainConfig.getFileName() + " has been created");
        }

        if(messageConfig.isNew())
        {
            this.getLogger().log(Level.INFO, messageConfig.getFileName() + " has been created.");
        }

        // Check if Factions support is enabled
        isFactions = mainConfig.getFileConfiguration().getBoolean("factions.support");
        if(isFactions)
        {
            if(plugMan.isPluginEnabled("Factions"))
            {
                minimumFactionRank = mainConfig.getFileConfiguration().getString("factions.minimum-rank");
                plugMan.registerEvents(new ListenUnclaim(this), this);
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
            if(plugMan.isPluginEnabled("PlotSquared"))
            {
                this.getLogger().log(Level.INFO, "PlotSquared support enabled!");
            }
            else
            {
                isPlots = false;
                this.getLogger().log(Level.SEVERE, "PlotSquared jar was not found!");
            }
        }

        try
        {
            messageManager = new MessageManager(messageConfig);
        }
        catch(Exception e)
        {
            e.printStackTrace();
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

        this.packMan.unloadAllPacketListeners();
        this.getLogger().log(Level.INFO, "Cleanup: All packet listeners have been removed");

        messageConfig.save();
        mainConfig.save();

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

    public PacketManager getPacketManager()
    {
        return packMan;
    }

    public MessageManager getMessageManager()
    {
        return this.messageManager;
    }

    public INMSHandler getNMS()
    {
        return nmsHandler;
    }

    public void setNMS(INMSHandler nmsHandler)
    {
        this.nmsHandler = nmsHandler;
    }

    public boolean isPlots()
    {
        return isPlots;
    }
}
