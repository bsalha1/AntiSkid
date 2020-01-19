/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.runnables;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractTask;
import com.reliableplugins.antiskid.type.Whitelist;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class TaskProtectRepeaters extends AbstractTask
{
    public TaskProtectRepeaters(AntiSkid antiSkid)
    {
        super(antiSkid, 0, antiSkid.getConfig().getInt("asynch-thread-period"));
//        super(antiSkid, 1);
    }

    @Override
    public void run()
    {
        Whitelist whitelist;

        // Replace all protected repeaters
        try
        {
            plugin.lock.acquire();
        }
        catch(Exception ignored) { }
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
        {
            whitelist = plugin.whitelists.get(entry.getKey());
            for(Set<Location> locs : entry.getValue().values())
            {
                for(Location loc : locs)
                {
                    plugin.getNMS().broadcastBlockChangePacket(Material.CARPET, loc, whitelist.getUUIDs());
                }
            }
        }
        plugin.lock.release();
    }
}
