/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.reliableplugins.antiskid.abstracts.AbstractPacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class RepeaterReplacePacket extends AbstractPacket
{
    private static final PacketType type = PacketType.Play.Server.BLOCK_CHANGE;

    public RepeaterReplacePacket(Block diode)
    {
        super(new PacketContainer(type), type);
        this.handle.getModifier().writeDefaults();
        this.handle.getBlockData().write(0, WrappedBlockData.createData(Material.CARPET));
        this.handle.getBlockPositionModifier().write(0, new BlockPosition(diode.getLocation().toVector()));
    }
}
