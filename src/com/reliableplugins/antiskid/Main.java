/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid;

import com.reliableplugins.antiskid.commands.CmdAntiSkid;
import com.reliableplugins.antiskid.listeners.ListenPlayerLeave;
import com.reliableplugins.antiskid.listeners.ListenRepeaterBreak;
import com.reliableplugins.antiskid.listeners.ListenRepeaterPlace;
import com.reliableplugins.antiskid.runnables.MaskRepeaters;
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
    public volatile Map<Player, Set<Block>> repeaterMap = new LinkedHashMap<>(); // Volatile because it is accessed in different threads
    public Map<Player, MaskRepeaters> tasks = new LinkedHashMap<>();
    public static final PluginManager plugMan = Bukkit.getPluginManager();

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
        plugMan.registerEvents(new ListenRepeaterPlace(this), this);
        plugMan.registerEvents(new ListenRepeaterBreak(this), this);
        plugMan.registerEvents(new ListenPlayerLeave(this), this);
    }
}
