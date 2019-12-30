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
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
import com.reliableplugins.antiskid.utils.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class AntiSkid extends JavaPlugin
{
    public volatile TreeMap<UUID, Map<Chunk, Set<Location>>> diodes = new TreeMap<>();
    public volatile TreeMap<UUID, TreeSet<UUID>> whitelists = new TreeMap<>();

    public static final PluginManager plugMan = Bukkit.getPluginManager();

    public static TreeMap<String, String> messages;
    public static MainConfig mainConfig;

    @Override
    public void onEnable()
    {
        loadConfigs();
        loadTasks();
        loadListeners();
        loadCommands();
    }

    @Override
    public void onDisable()
    {
        // Reveal all protected diodes
        for (Map<Chunk, Set<Location>> chunkSetMap : diodes.values())
        {
            for(Set<Location> locs : chunkSetMap.values())
            {
                for(Location loc : locs)
                {
                    new RepeaterRevealPacket(loc).broadcastPacket();
                }
            }
        }

        // Unload NMS packet listeners
        for(Player p : this.getServer().getOnlinePlayers())
        {
            PacketUtil.unloadPacketListeners(p);
        }
        this.saveConfig();
    }

    private void loadConfigs()
    {
        mainConfig = new MainConfig(this, "config.yml");
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

        for(Player p : this.getServer().getOnlinePlayers())
        {
            PacketUtil.loadPacketListener(new ListenBlockChangePacket(this, p), p);
        }
    }
}
