/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.reliableplugins.antiskid.AntiSkid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class ListenBlockChangePacket extends PacketAdapter
{
    private AntiSkid antiSkid;

    public ListenBlockChangePacket(AntiSkid antiSkid, PacketType... types)
    {
        super(antiSkid, types);
        this.antiSkid = antiSkid;
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        if(!event.getPacket().getBlockData().read(0).equals(WrappedBlockData.createData(Material.DIODE_BLOCK_OFF))) return;
        Player player = event.getPlayer();
        BlockPosition blockPos = event.getPacket().getBlockPositionModifier().read(0);
        Block block = blockPos.toLocation(player.getLocation().getWorld()).getBlock();

        for(Map.Entry<Player, Set<Block>> entry : antiSkid.diodeMap.entrySet())
        {
            if(entry.getValue().contains(block)) // If protected diode
            {
                if(entry.getKey().equals(player)) return; // If it's the player's diode, allow them to see it
                event.setCancelled(true); // Keep diode hidden
                return;
            }
        }
    }
}
