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

    protected AbstractTask(Main main)
    {
        this.main = main;
    }

    public int getId() { return this.id; }

    public void cancel() { Bukkit.getScheduler().cancelTask(this.id); }
}
