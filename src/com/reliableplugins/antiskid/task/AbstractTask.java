/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.task;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Bukkit;

public abstract class AbstractTask implements Runnable
{
    protected int id;
    protected AntiSkid plugin;

    // Set delayed task
    public AbstractTask(AntiSkid plugin, long delay)
    {
        this.plugin = plugin;
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, delay);
    }

    // Set delayed repeating task
    public AbstractTask(AntiSkid plugin, long delay, long period)
    {
        this.plugin = plugin;
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
    }

    public int getId()
    {
        return this.id;
    }

    public void cancel()
    {
        Bukkit.getScheduler().cancelTask(this.id);
    }
}
