package com.reliableplugins.antiskid;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.reliableplugins.antiskid.listen.ListenRepeaterPlace;
import com.reliableplugins.antiskid.runnable.IterateRepeaters;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Main extends JavaPlugin
{
    public volatile Map<Chunk, Set<Block>> repeaters = new LinkedHashMap<>();
    public Runnable iterateRepeaters;
    public static final ProtocolManager protMan = ProtocolLibrary.getProtocolManager();
    public static final PluginManager plugMan = Bukkit.getPluginManager();

    public void onEnable()
    {
        loadTasks();
        loadCommands();
        loadListeners();
    }

    public void onDisable()
    {

    }

    private void loadTasks()
    {
        iterateRepeaters = new IterateRepeaters(this);
    }

    private void loadCommands(){}

    private void loadListeners()
    {
        this.plugMan.registerEvents(new ListenRepeaterPlace(this), this);
    }
}
