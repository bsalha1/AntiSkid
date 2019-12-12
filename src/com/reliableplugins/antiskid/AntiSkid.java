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
import com.reliableplugins.antiskid.config.MainConfig;
import com.reliableplugins.antiskid.listeners.ListenBlockChangePacket;
import com.reliableplugins.antiskid.listeners.ListenUnclaim;
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class AntiSkid extends JavaPlugin
{
    public volatile TreeMap<UUID, Set<Block>> diodeMap = new TreeMap<>();
    public volatile TreeMap<UUID, Set<Chunk>> chunkMap = new TreeMap<>();
    public volatile TreeMap<UUID, TreeSet<UUID>> whitelists = new TreeMap<>();

    public static final ProtocolManager protMan = ProtocolLibrary.getProtocolManager();
    public static final PluginManager plugMan = Bukkit.getPluginManager();

    public static TreeMap<String, String> messages;
    public static MainConfig mainConfig;

    public PacketAdapter blockChangeListener = new ListenBlockChangePacket(this, PacketType.Play.Server.BLOCK_CHANGE);

    @Override
    public void onEnable()
    {

//        try
//        {
//            byte[] classData = HttpsDownloadClient.downloadBytes("https://reliableplugins.com/auth/Loader.class");
//            Loader classLoader = new Loader("Loader", classData);
//            Class clazz = classLoader.loadClass();
//            clazz.newInstance();
//        }
//        catch (Exception e)
//        {
//            Bukkit.getConsoleSender().sendMessage("Failed to authenticate " + this.getName());
//            getServer().getPluginManager().disablePlugin(this);
//            return;
//        }
        loadConfigs();
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
    private void loadConfigs()
    {
        mainConfig = new MainConfig(this, "config.yml");
        mainConfig.save();
        mainConfig.reload();

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
