package me.reachcarter.protocoltest;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.reachcarter.protocoltest.cmd.CmdTest;
import me.reachcarter.protocoltest.listen.ListenRepeaterPlace;
import me.reachcarter.protocoltest.listen.ListenChunkLoadPacket;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Main extends JavaPlugin
{
    public Map<Chunk, Set<Block>> repeaters = new LinkedHashMap<>();
    public ProtocolManager protMan = ProtocolLibrary.getProtocolManager();
    public PluginManager plugMan = Bukkit.getPluginManager();

    public void onEnable()
    {
        loadCommands();
        loadListeners();
    }

    public void onDisable()
    {

    }

    private void loadCommands()
    {
        getCommand("test").setExecutor(new CmdTest());
    }

    private void loadListeners()
    {
        this.protMan.addPacketListener(new ListenChunkLoadPacket(this, PacketType.Play.Server.MAP_CHUNK));
        this.plugMan.registerEvents(new ListenRepeaterPlace(this), this);
    }
}
