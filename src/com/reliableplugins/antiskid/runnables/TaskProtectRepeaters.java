/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.runnables;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.abstracts.AbstractTask;
import com.reliableplugins.antiskid.packets.RepeaterReplacePacket;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class TaskProtectRepeaters extends AbstractTask
{
    public TaskProtectRepeaters(Main main)
    {
        super(main, 0, 20);
    }

    @Override
    public void run()
    {
        Set<Player> whitelist;

        // For all diode maps
        for(Map.Entry<Player, Set<Block>> entry : main.diodeMap.entrySet())
        {
            // Send block changes to all players not in whitelist (who have the chunk loaded)
            whitelist = main.whitelists.get(entry.getKey());
            for(Block b : entry.getValue())
            {
                new RepeaterReplacePacket(b).broadcastPacket(whitelist);
            }
        }
    }
}
