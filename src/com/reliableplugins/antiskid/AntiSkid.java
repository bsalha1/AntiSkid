/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid;

import com.reliableplugins.antiskid.commands.Base_CommandAntiSkid;
import com.reliableplugins.antiskid.config.MainConfig;
import com.reliableplugins.antiskid.config.MessageConfig;
import com.reliableplugins.antiskid.listeners.ListenBlockChangePacket;
import com.reliableplugins.antiskid.listeners.ListenDiodePlace;
import com.reliableplugins.antiskid.listeners.ListenPlayerJoin;
import com.reliableplugins.antiskid.listeners.ListenUnclaim;
import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.nms.NMSManager;
import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
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
    public volatile TreeMap<UUID, TreeSet<UUID>> whitelists = new TreeMap<>();

    public volatile Semaphore lock = new Semaphore(1);
    private boolean isFactions;
    private INMSHandler nmsHandler;
    private NMSManager nmsManager = new NMSManager(this);
    public static final PluginManager plugMan = Bukkit.getPluginManager();
    public final PacketManager packMan = new PacketManager(this);

    public static MessageConfig messageConfig;
    public static MainConfig mainConfig;

    @Override
    public void onEnable()
    {
        messageConfig = new MessageConfig(this, "messages.yml");
        messageConfig.save();
        messageConfig.load();

        mainConfig = new MainConfig(this, "config.yml");
        mainConfig.save();
        messageConfig.load();

        isFactions = mainConfig.getFileConfiguration().getBoolean("factions-support");

        new TaskProtectRepeaters(this);
        new Base_CommandAntiSkid(this);

        if(isFactions)
        {
            plugMan.registerEvents(new ListenUnclaim(this), this);
            this.getLogger().log(Level.INFO, "Factions support enabled!");
        }
        else
        {
            this.getLogger().log(Level.WARNING, "Factions support is disabled. Go to plugins/AntiSkid/config.yml and set factions-support = true");
        }
        plugMan.registerEvents(new ListenPlayerJoin(this), this);
        plugMan.registerEvents(new ListenDiodePlace(this), this);
        packMan.loadPacketListener(new ListenBlockChangePacket(this));

        this.getLogger().log(Level.INFO, "AntiSkid v1.0 has been loaded");
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

    public INMSHandler getNMS()
    {
        return nmsHandler;
    }

    public void setNMS(INMSHandler nmsHandler)
    {
        this.nmsHandler = nmsHandler;
    }
}
