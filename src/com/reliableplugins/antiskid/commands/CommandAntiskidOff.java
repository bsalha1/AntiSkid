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
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
        Map<Chunk, Set<Location >> diodes = plugin.diodes.get(executorId);
        TreeSet<UUID> whitelist = plugin.whitelists.get(executorId);


        // If there are no diodes registered
        if (diodes == null)
        {
            executor.sendMessage(Message.ERROR_NOT_PROTECTED.toString());
            return;
        }


        for (Set<Location> locs : diodes.values())
        {
            // Revert the diode for all blacklisted players
            for(Location loc : locs)
            {
                new RepeaterRevealPacket(loc).broadcastPacket(whitelist);
            }
        }

        plugin.diodes.remove(executorId);
        executor.sendMessage(Message.ANTISKID_OFF.toString());
    }
}
