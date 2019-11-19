package me.reachcarter.protocoltest.listen;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.reachcarter.protocoltest.Main;
import me.reachcarter.protocoltest.packet.RepeaterAlterPacket;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public class ListenChunkLoadPacket extends PacketAdapter
{
    private Main main;

    public ListenChunkLoadPacket(Main main, PacketType... types)
    {
        super(main, types);
        this.main = main;
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        // When chunk is loaded
        Player player = event.getPlayer();
        PacketContainer pckt = event.getPacket();
        int chunkX = pckt.getIntegers().read(0);
        int chunkZ = pckt.getIntegers().read(1);
        Chunk chunk = player.getWorld().getChunkAt(chunkX, chunkZ);

        // If there are repeaters in this chunk, shuffle them
        if(main.repeaters.containsKey(chunk))
        {
            shuffle(player, chunk);
        }
    }

    private void shuffle(Player player, Chunk chunk)
    {
        Set<Block> repeaterBlocks = main.repeaters.get(chunk);

        // Foreach repeater in the chunk, try sending a block change to the player
        for(Block b : repeaterBlocks)
        {
            try
            {
                main.protMan.sendServerPacket(player, new RepeaterAlterPacket(b.getLocation()));
            }
            catch(Exception e)
            {
                main.getServer().getConsoleSender().sendMessage("Failed to send repeater alter packet");
            }
        }
    }
}
