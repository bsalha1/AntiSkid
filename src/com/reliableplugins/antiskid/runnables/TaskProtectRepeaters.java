/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.runnables;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractTask;
import com.reliableplugins.antiskid.packets.RepeaterHidePacket;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class TaskProtectRepeaters extends AbstractTask
{
    public TaskProtectRepeaters(AntiSkid antiSkid)
    {
//        super(antiSkid, 0, antiSkid.getConfig().getInt("asynch-thread-period"));
        super(antiSkid, 1);
    }

    @Override
    public void run()
    {
        TreeSet<UUID> whitelist;

        // Replace all protected repeaters
        for(Map.Entry<UUID, Map<Chunk, Set<Location>>> entry : plugin.diodes.entrySet())
        {
            whitelist = plugin.whitelists.get(entry.getKey());
            for(Set<Location> locs : entry.getValue().values())
            {
                for(Location loc : locs)
                {
                    new RepeaterHidePacket(loc).broadcastPacket(whitelist);
                }
            }
        }
    }
}
