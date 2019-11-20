package com.reliableplugins.antiskid.runnable;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.packet.RepeaterAlterPacket;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class IterateRepeaters implements Runnable
{
    private int id;
    private static int period = 200;
    private Main main;

    public IterateRepeaters(Main main)
    {
        this.main = main;
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, 0L, period);
    }



    @Override
    public void run()
    {
        for(Map.Entry<Chunk, Set<Block>> r : main.repeaters.entrySet()) // Iterate thru repeaters map
        {
            // If chunk isn't loaded, don't iterate
            if(!r.getKey().isLoaded()) continue;

            // If the chunk is loaded, iterate thru online players
            for(Player p : Bukkit.getOnlinePlayers())
            {
                // Whitelist players here
                if(p.getPlayerListName().equalsIgnoreCase("ReachCarter")) continue;

                // Iterate thru the repeater blocks in the chunk
                for(Block b : r.getValue())
                {
                    // Change the appearance of the repeater
                    try
                    {
                        main.protMan.sendServerPacket(p, new RepeaterAlterPacket(b.getLocation()).packet);
                    }
                    catch(Exception e)
                    {
                        Bukkit.getConsoleSender().sendMessage(e.toString());
                    }
                }
            }
        }
    }


    /*
        Cancels the task
     */
    public void cancel()
    {
        Bukkit.getScheduler().cancelTask(this.id);
    }
}
