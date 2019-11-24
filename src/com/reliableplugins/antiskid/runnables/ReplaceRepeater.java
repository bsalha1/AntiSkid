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

public class ReplaceRepeater extends AbstractTask
{
    private Block block;
    private Player player;

    public ReplaceRepeater(Main main, Block block, Player player)
    {
        super(main);
        this.block = block;
        this.player = player;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, this, 2);
    }

    @Override
    public void run()
    {
        new RepeaterReplacePacket(block).sendPacket(player);
    }
}
