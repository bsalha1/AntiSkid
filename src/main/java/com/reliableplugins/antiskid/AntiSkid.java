package com.reliableplugins.antiskid;

import com.reliableplugins.antiskid.commands.*;
import com.reliableplugins.antiskid.config.FileManager;
import com.reliableplugins.antiskid.config.MainConfig;
import com.reliableplugins.antiskid.config.MessageConfig;
import com.reliableplugins.antiskid.hook.PlotSquaredHook;
import com.reliableplugins.antiskid.listeners.*;
import com.reliableplugins.antiskid.nms.*;
import com.reliableplugins.antiskid.type.Pair;
import com.reliableplugins.antiskid.type.Whitelist;
import com.reliableplugins.antiskid.utils.BukkitUtil;
import com.reliableplugins.antiskid.utils.Cache;
import com.reliableplugins.antiskid.utils.ChannelManager;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

public class AntiSkid extends JavaPlugin implements Listener
{
    // Memory Database
    public volatile HashMap<UUID, Map<Chunk, Set<Location>>> diodes = new HashMap<>();
    public volatile HashMap<UUID, Whitelist> whitelists = new HashMap<>();
    public volatile HashMap<UUID, Pair<Location, Location>> selectionPoints = new HashMap<>();
    public volatile Cache cache;
    public static AntiSkid INSTANCE;

    private volatile Semaphore lock;

    private ANMSHandler nmsHandler;

    private ChannelManager listenerManager;

    private String version;
    private Material replacer;

    private FileManager fileManager;
    private MainConfig mainConfig;
    private MessageConfig messageConfig;

    @Override
    public void onEnable()
    {
        AntiSkid.INSTANCE = this;
        replacer = Material.REDSTONE_COMPARATOR_OFF;

        lock = new Semaphore(1);
        PluginManager pluginManager = Bukkit.getPluginManager();

        initConfigs();

        nmsHandler = getNMSHandler();

        CommandHandler cmdHandler = new CommandHandler();
        cmdHandler.addCommand(new CommandOn());
        cmdHandler.addCommand(new CommandTool());
        cmdHandler.addCommand(new CommandOff());
        cmdHandler.addCommand(new CommandWhitelist());
        cmdHandler.addCommand(new CommandReload());
        cmdHandler.addCommand(new CommandClear());

        try
        {
            InputStream initialStream = getResource("README.md");
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            File targetFile = new File(getDataFolder(), "README.md");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        listenerManager = new ChannelManager();
        pluginManager.registerEvents(new ListenPlayerLoginLogout(), this);
        pluginManager.registerEvents(new ListenDiodeAction(), this);
        pluginManager.registerEvents(new ListenUseSelectionTool(), this);
        listenerManager.loadChannelListener(new ChannelListener());
        cache = new Cache();

        getLogger().log(Level.INFO, this.getDescription().getName() + "v" + this.getDescription().getVersion()  + " has been loaded");
    }

    public void initConfigs()
    {
        fileManager = new FileManager();
        fileManager.addFile(mainConfig = new MainConfig());
        fileManager.addFile(messageConfig = new MessageConfig());
    }

    private ANMSHandler getNMSHandler()
    {
        String nmsVersion = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        switch(nmsVersion)
        {
            case "v1_8_R2":
                return new Version_1_8_R2();
            case "v1_8_R3":
                return new Version_1_8_R3();
            case "v_1_9_R1":
                return new Version_1_9_R1();
            case "v_1_9_R2":
                return new Version_1_9_R2();
            case "v_1_10_R1":
                return new Version_1_10_R1();
            case "v1_11_R1":
                return new Version_1_11_R1();
            case "v1_12_R1":
                return new Version_1_12_R1();
            case "v1_13_R1":
                return new Version_1_13_R1();
            case "v1_13_R2":
                return new Version_1_13_R2();
            case "v1_14_R1":
                return new Version_1_14_R1();
            case "v1_15_R1":
            default:
                return new Version_1_15_R1();
        }
    }

    @Override
    public void onDisable()
    {
        getLogger().log(Level.INFO, "Cleanup: Start cleanup");
        for(Map<Chunk, Set<Location>> chunksMap : diodes.values())
        {
            for(Chunk chunk : chunksMap.keySet())
            {
                BukkitUtil.reloadChunk(chunk);
            }
        }
        getLogger().log(Level.INFO, "Cleanup: All protected repeaters have been revealed");

        listenerManager.unloadChannelListener();
        getLogger().log(Level.INFO, "Cleanup: All packet listeners have been removed");

        getLogger().log(Level.INFO, this.getDescription().getName() + "v" + this.getDescription().getVersion()  + " has been unloaded");
    }

    public void startSynchronousTask(Runnable task)
    {
        try
        {
            lock.acquire();
        }
        catch(Exception ignored){}
        task.run();
        lock.release();
    }

    public FileManager getFileManager()
    {
        return fileManager;
    }

    public MainConfig getMainConfig()
    {
        return mainConfig;
    }

    public ChannelManager getPacketManager()
    {
        return listenerManager;
    }

    public ANMSHandler getNMS()
    {
        return nmsHandler;
    }

    public Material getReplacer()
    {
        return replacer;
    }

    public MessageConfig getMessageConfig()
    {
        return messageConfig;
    }
}
