/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

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
import java.util.UUID;
import java.util.concurrent.Executors;

@CommandBuilder(label = "off", permission = "antiskid.off", playerRequired = true)
public class CommandAntiskidOff extends AbstractCommand
{
    private Player executor;
    private UUID executorId;

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        this.executor = (Player) sender;
        this.executorId = executor.getUniqueId();
        Executors.newSingleThreadExecutor().submit(this::antiskidOff);
    }

    private void antiskidOff()
    {
        Set<Block> diodes = plugin.diodeMap.get(executorId);
        Set<Player> whitelist = plugin.whitelists.get(executorId);

        if(diodes == null) // If there are no diodes registered
        {
            executor.sendMessage(Message.ERROR_NO_PROTECTED.toString());
            return;
        }

        AntiSkid.protMan.removePacketListener(plugin.blockChangeListener);
        for(Block b : diodes) new RepeaterRevealPacket(b).broadcastPacket(whitelist); // Revert the diode for all blacklisted players
        AntiSkid.protMan.addPacketListener(plugin.blockChangeListener);

        plugin.diodeMap.remove(executorId);
        executor.sendMessage(Message.ANTISKID_OFF.toString());
    }
}
