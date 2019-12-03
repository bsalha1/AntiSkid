/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.abstracts;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Bukkit;

public abstract class AbstractTask implements Runnable
{
    protected int id;
    protected AntiSkid antiSkid;

    // Set delayed task
    public AbstractTask(AntiSkid antiSkid, long delay)
    {
        this.antiSkid = antiSkid;
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(antiSkid, this, delay);
    }

    // Set delayed repeating task
    public AbstractTask(AntiSkid antiSkid, long delay, long period)
    {
        this.antiSkid = antiSkid;
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(antiSkid, this, delay, period);
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
