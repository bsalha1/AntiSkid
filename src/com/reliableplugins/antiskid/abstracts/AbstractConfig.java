/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.abstracts;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConfig
{
    protected AntiSkid plugin;
    protected Map<String, Object> defaults = new HashMap<>();
    private File file;
    protected FileConfiguration config;

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
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save()
    {
        try
        {
            config.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void loadDefaults()
    {
        this.config.addDefaults(defaults);
        this.config.options().copyDefaults(true);
    }

    public void addDefault(String path, Object value)
    {
        this.defaults.put(path, value);
    }

    public void remDefault(String path)
    {
        this.defaults.remove(path);
    }

    public void reload()
    {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public File getFile()
    {
        return this.file;
    }

    public FileConfiguration getFileConfiguration()
    {
        return this.config;
    }
}
