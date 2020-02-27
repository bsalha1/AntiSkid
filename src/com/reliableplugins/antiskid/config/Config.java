/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class Config
{
    protected AntiSkid plugin;
    private boolean isNew = false;
    private File file;
    protected FileConfiguration config;
    protected Map<String, Object> defaults = new LinkedHashMap<>();

    public Config(AntiSkid plugin, String fileName)
    {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists())
        {
            isNew = true;
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void loadDefaults()
    {
        config.addDefaults(defaults);
        config.options().copyDefaults(true);

        try
        {
            config.save(file);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    public void save()
    {
        try
        {
            config = YamlConfiguration.loadConfiguration(file);
            config.save(file);
            plugin.getLogger().log(Level.INFO, file.getName() + " has been saved");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getFileName()
    {
        return file.getName();
    }

    public boolean isNew()
    {
        return isNew;
    }

    protected void addDefault(String key, Object value)
    {
        this.defaults.put(key, value);
    }

    public Map<String, Object> getDefaults()
    {
        return defaults;
    }

    public FileConfiguration getFileConfiguration()
    {
        return this.config;
    }
}
