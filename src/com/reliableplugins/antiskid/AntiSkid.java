/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.reliableplugins.antiskid.commands.Base_CommandAntiSkid;
import com.reliableplugins.antiskid.listeners.ListenBlockChangePacket;
import com.reliableplugins.antiskid.listeners.ListenUnclaim;
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class AntiSkid extends JavaPlugin
{
    public volatile TreeMap<UUID, Set<Block>> diodeMap = new TreeMap<>();
    public volatile TreeMap<UUID, Set<Chunk>> chunkMap = new TreeMap<>();
    public volatile TreeMap<UUID, Set<Player>> whitelists = new TreeMap<>();

    public static final ProtocolManager protMan = ProtocolLibrary.getProtocolManager();
    public static final PluginManager plugMan = Bukkit.getPluginManager();

    public PacketAdapter blockChangeListener = new ListenBlockChangePacket(this, PacketType.Play.Server.BLOCK_CHANGE);

    @Override
    public void onEnable()
    {
        loadConfig();
        loadTasks();
        loadListeners();
        loadCommands();
    }

    @Override
    public void onDisable()
    {
        AntiSkid.protMan.removePacketListener(blockChangeListener);
        for(Map.Entry<UUID, Set<Block>> entry : diodeMap.entrySet())
        {
            for(Block b : entry.getValue())
            {
                new RepeaterRevealPacket(b).broadcastPacket();
            }
        }
        AntiSkid.protMan.addPacketListener(blockChangeListener);
        this.saveConfig();
    }


    /**
     * Loads the config
     */
    private void loadConfig()
    {
        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("asynch-thread-period", 20);
        this.getConfig().addDefaults(defaults);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }


    /**
     * Loads tasks
     */
    private void loadTasks()
    {
        new TaskProtectRepeaters(this);
    }


    /**
     * Loads commands
     */
    private void loadCommands()
    {
        new Base_CommandAntiSkid(this);
    }


    /**
     * Loads listeners
     */
    private void loadListeners()
    {
        plugMan.registerEvents(new ListenUnclaim(this), this);
        protMan.addPacketListener(blockChangeListener);
    }
}
