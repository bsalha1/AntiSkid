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
import com.reliableplugins.antiskid.items.AntiSkidTool;
import com.reliableplugins.antiskid.listeners.*;
import com.reliableplugins.antiskid.runnables.TaskProtectRepeaters;
import javafx.util.Pair;
import org.bukkit.Bukkit;
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
    public volatile Map<Player, Set<Player>> whitelists = new LinkedHashMap<>();
    public volatile Map<Player, Pair<Location, Location>> toolPoints = new LinkedHashMap<>();

    public static final PluginManager plugMan = Bukkit.getPluginManager();
    public static final ProtocolManager protMan = ProtocolLibrary.getProtocolManager();

    public PacketAdapter blockChangeListener = new ListenBlockChangePacket(this, PacketType.Play.Server.BLOCK_CHANGE);

    public void onEnable()
    {
        loadTasks();
        loadItems();
        loadCommands();
        loadListeners();
    }

    public void onDisable()
    {

    }

    /**
     * Loads tasks
     */
    private void loadTasks()
    {
        new TaskProtectRepeaters(this);
    }

    /**
     * Loads item handlers
     */
    private void loadItems()
    {
        new AntiSkidTool(this, new AntiSkidToolHandler(this));
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

        plugMan.registerEvents(new ListenRepeaterBreak(this), this);
    }
}
