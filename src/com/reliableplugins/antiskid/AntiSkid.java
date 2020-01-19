/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid;

import com.reliableplugins.antiskid.commands.Base_CommandAntiSkid;
import com.reliableplugins.antiskid.config.MainConfig;
import com.reliableplugins.antiskid.listeners.ListenBlockChangePacket;
import com.reliableplugins.antiskid.listeners.ListenDiodePlace;
import com.reliableplugins.antiskid.listeners.ListenPlayerJoin;
import com.reliableplugins.antiskid.listeners.ListenUnclaim;
import com.reliableplugins.antiskid.nms.INMSHandler;
import com.reliableplugins.antiskid.nms.NMSManager;
import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
import com.reliableplugins.antiskid.utils.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class AntiSkid extends JavaPlugin
{
    public volatile TreeMap<UUID, Map<Chunk, Set<Location>>> diodes = new TreeMap<>();
    public volatile TreeMap<UUID, TreeSet<UUID>> whitelists = new TreeMap<>();

    private INMSHandler nmsHandler;
    private NMSManager nmsManager;
    public static final PluginManager plugMan = Bukkit.getPluginManager();
    public final PacketManager packMan = new PacketManager(this.getServer());

    public static MainConfig mainConfig;

    @Override
    public void onEnable()
    {
        nmsManager = new NMSManager(this);
        loadConfigs();
        loadTasks();
        loadListeners();
        loadCommands();
    }

    @Override
    public void onDisable()
    {
        // Reveal all protected diodes
        for(Map<Chunk, Set<Location>> chunksMap : diodes.values())
        {
            for(Chunk chunk : chunksMap.keySet())
            {
                chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
            }
        }

        this.packMan.unloadAllPacketListeners();
        mainConfig.save();
    }

    private void loadConfigs()
    {
        mainConfig = new MainConfig(this, "messages.yml");
        mainConfig.save();
        mainConfig.reload();
    }

    private void loadTasks()
    {
        new TaskProtectRepeaters(this);
    }

    private void loadCommands()
    {
        new Base_CommandAntiSkid(this);
    }

    private void loadListeners()
    {
        plugMan.registerEvents(new ListenUnclaim(this), this);
        plugMan.registerEvents(new ListenPlayerJoin(this), this);
        plugMan.registerEvents(new ListenDiodePlace(this), this);

        packMan.loadPacketListener(new ListenBlockChangePacket(this));
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
