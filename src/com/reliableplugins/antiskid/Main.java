/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.reliableplugins.antiskid.commands.CmdAntiSkid;
import com.reliableplugins.antiskid.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Main extends JavaPlugin
{
    public volatile Map<Player, Set<Block>> diodeMap = new LinkedHashMap<>(); // Volatile because it is accessed in different threads
    public volatile Set<Player> executors = new HashSet<>();
    public static final PluginManager plugMan = Bukkit.getPluginManager();
    public static final ProtocolManager protMan = ProtocolLibrary.getProtocolManager();

    public void onEnable()
    {
        loadCommands();
        loadListeners();
    }

    public void onDisable()
    {

    }

    private void loadCommands()
    {
        getCommand("antiskid").setExecutor(new CmdAntiSkid(this));
    }

    private void loadListeners()
    {
        protMan.addPacketListener(new ListenBlockChangePacket(this, PacketType.Play.Server.BLOCK_CHANGE));

        plugMan.registerEvents(new ListenRepeaterAlter(this), this);
        plugMan.registerEvents(new ListenRepeaterBreak(this), this);
    }
}
