/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.reliableplugins.antiskid.commands.CmdAntiSkid;
import com.reliableplugins.antiskid.listeners.ListenBlockChangePacket;
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AntiSkid extends JavaPlugin
{
    public volatile Map<Player, Set<Block>> diodeMap = new LinkedHashMap<>();
    public volatile Map<Player, Set<Chunk>> chunkMap = new LinkedHashMap<>();
    public volatile Map<Player, Set<Player>> whitelists = new LinkedHashMap<>();

    public static final PluginManager plugMan = Bukkit.getPluginManager();
    public static final ProtocolManager protMan = ProtocolLibrary.getProtocolManager();

    public PacketAdapter blockChangeListener = new ListenBlockChangePacket(this, PacketType.Play.Server.BLOCK_CHANGE);

    @Override
    public void onEnable()
    {
        loadConfig();
        loadTasks();
        loadCommands();
        loadListeners();
    }

    @Override
    public void onDisable()
    {
        for(Map.Entry<Player, Set<Block>> entry : diodeMap.entrySet())
        {
            for(Block b : entry.getValue())
            {
                new RepeaterRevealPacket(b).broadcastPacket();
            }
        }
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
        getCommand("antiskid").setExecutor(new CmdAntiSkid(this));
    }


    /**
     * Loads listeners
     */
    private void loadListeners()
    {
        protMan.addPacketListener(blockChangeListener);
    }
}
