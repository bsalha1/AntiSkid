package me.reachcarter.protocoltest.cmd;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.reachcarter.protocoltest.packet.RepeaterAlterPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTest implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if(!(commandSender instanceof Player))
        {
            return false;
        }

        Player player = (Player) commandSender;

        try
        {
            Bukkit.broadcastMessage("Sending packet");
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, new RepeaterAlterPacket(player.getLocation()));
        }
        catch(Exception e)
        {
            Bukkit.broadcastMessage("Failed");
            return false;
        }

        return true;
    }
}
