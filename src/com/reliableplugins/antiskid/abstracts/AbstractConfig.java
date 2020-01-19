/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.abstracts;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractConfig
{
    protected AntiSkid plugin;
    private File file;
    protected FileConfiguration config;
    protected Map<String, Object> defaults = new LinkedHashMap<>();

    public AbstractConfig(AntiSkid plugin, String fileName)
    {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists())
        {
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
    }


    public void save()
    {
        try
        {
            config.save(file);
            plugin.getLogger().log(Level.INFO, file.getName() + " has been saved");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void load()
    {
        config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().log(Level.INFO, file.getName() + " has been loaded");
    }

    protected void addDefault(String key, Object value)
    {
        this.defaults.put(key, value);
    }

    public FileConfiguration getFileConfiguration()
    {
        return this.config;
    }
}
