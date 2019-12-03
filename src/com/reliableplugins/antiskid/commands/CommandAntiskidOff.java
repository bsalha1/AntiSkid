/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.commands;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractCommand;
import com.reliableplugins.antiskid.annotation.CommandBuilder;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.packets.RepeaterRevealPacket;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

@CommandBuilder(label = "off", permission = "antiskid.off", playerRequired = true)
public class CommandAntiskidOff extends AbstractCommand
{
    @Override
    public void execute(CommandSender executor, String[] args)
    {
        Set<Block> diodes = plugin.diodeMap.get(executor);
        Set<Player> whitelist = plugin.whitelists.get(executor);

        if(diodes == null) // If there are no diodes registered
        {
            executor.sendMessage(Message.ERROR_NO_PROTECTED.toString());
            return;
        }

        AntiSkid.protMan.removePacketListener(plugin.blockChangeListener);
        for(Block b : diodes) new RepeaterRevealPacket(b).broadcastPacket(whitelist); // Revert the diode for all blacklisted players
        AntiSkid.protMan.addPacketListener(plugin.blockChangeListener);

        plugin.diodeMap.remove(executor);
        executor.sendMessage(Message.ANTISKID_OFF.toString());
    }
}
