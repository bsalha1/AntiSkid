/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.runnables;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.packets.RepeaterShufflePacket;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

public class MaskRepeaters implements Runnable
{
    private int id;
    private static int period = 4;
    private Main main;
    private Player player;

    public MaskRepeaters(Main main, Player player)
    {
        this.main = main;
        this.player = player;
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, 0L, period);
    }


    @Override
    public void run()
    {
        Set<Block> blockSet = this.main.repeaterMap.get(player);
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        if(blockSet == null) return;
        long start = System.nanoTime();
        for(Block b : blockSet)
        {
            if(!b.getLocation().getChunk().isLoaded()) continue;

            for(Player p : onlinePlayers)
            {
                if(p.equals(this.player)) continue;
                new RepeaterShufflePacket(b).sendPacket(p);
            }
        }
        long end = System.nanoTime();
        double elapse = (end - start) / Math.pow(10, 9);
        Bukkit.broadcastMessage("Took " + Double.toString(elapse) + "sec");
    }



    /**
     * Cancels the task
     */
    public void cancel()
    {
        Bukkit.getScheduler().cancelTask(this.id);
    }
}
