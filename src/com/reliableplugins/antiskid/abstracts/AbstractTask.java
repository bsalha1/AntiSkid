/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.abstracts;

import com.reliableplugins.antiskid.Main;
import org.bukkit.Bukkit;

public abstract class AbstractTask implements Runnable
{
    protected int id;
    protected Main main;

    // Set delayed task
    public AbstractTask(Main main, long delay)
    {
        this.main = main;
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(main, this, delay);
    }

    // Set delayed repeating task
    public AbstractTask(Main main, long delay, long period)
    {
        this.main = main;
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, delay, period);
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
