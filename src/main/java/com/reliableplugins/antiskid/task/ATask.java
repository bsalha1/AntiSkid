/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.task;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Bukkit;

public abstract class ATask implements Runnable
{
    protected int id;

    // Set delayed task
    public ATask(long delay)
    {
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(AntiSkid.INSTANCE, this, delay);
    }

    // Set delayed repeating task
    public ATask(long delay, long period)
    {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(AntiSkid.INSTANCE, this, delay, period);
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
