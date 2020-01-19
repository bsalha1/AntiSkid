/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractConfig;

public class MainConfig extends AbstractConfig
{
    public MainConfig(AntiSkid plugin, String fileName)
    {
        super(plugin, fileName);

        addDefault("factions-support", true);
        addDefault("asynch-thread-period", 20);

        loadDefaults();
    }
}
