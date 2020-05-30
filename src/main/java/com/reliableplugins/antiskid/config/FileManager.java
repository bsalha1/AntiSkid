package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.AntiSkid;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class FileManager
{
    private AntiSkid plugin;
    private List<Config> files = new ArrayList<>();

    public FileManager(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    public void addFile(Config file)
    {
        files.add(file);
        plugin.getLogger().log(Level.INFO, file.getConfigFile().getName() + " has initialized.");
        file.load();
    }

    public List<Config> getFiles()
    {
        return files;
    }

    public void loadAll()
    {
        for(Config file : files)
        {
            file.load();
        }
    }

    public void saveAll()
    {
        for(Config file : files)
        {
            file.save();
        }
    }
}
