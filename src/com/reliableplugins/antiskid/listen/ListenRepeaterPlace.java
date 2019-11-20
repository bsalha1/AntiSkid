package com.reliableplugins.antiskid.listen;

import com.reliableplugins.antiskid.Main;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;

public class ListenRepeaterPlace implements Listener
{
    private Main main;

    public ListenRepeaterPlace(Main main)
    {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block.getType() != Material.DIODE_BLOCK_OFF) return;

        Chunk chunk = event.getPlayer().getLocation().getChunk();
        if(!main.repeaters.containsKey(chunk))
        {
            Set<Block> blockSet = new HashSet<>();
            blockSet.add(block);
            main.repeaters.put(chunk, blockSet);
            return;
        }

        main.repeaters.get(chunk).add(block);
        return;
    }
}
