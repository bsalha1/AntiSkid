/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.World;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainConfig extends Config
{
    public MainConfig(AntiSkid plugin, String fileName)
    {
        super(plugin, fileName);

        addDefault("fast-scan", true);
        if(this.isNew())
        {
            this.getFileConfiguration().createSection("factions");
//            this.getFileConfiguration().createSection("plotsquared");
        }
        addDefault("factions.support", true);
        addDefault("factions.minimum-rank", "moderator");
        addDefault("factions.whitelist-faction", true);
        List<String> worlds = new ArrayList<>();
        for(World world : plugin.getServer().getWorlds())
        {
            worlds.add(world.getName());
        }
        addDefault("factions.worlds", worlds);

//        addDefault("plotsquared.support", true);
//        addDefault("plotsquared.worlds", worlds);

        loadDefaults();
    }
}
