package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.type.Pair;
import com.reliableplugins.antiskid.type.SelectionTool;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenUseSelectionTool implements Listener
{
    private AntiSkid plugin;

    public ListenUseSelectionTool(AntiSkid plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getItem() == null || !event.getItem().equals(SelectionTool.getItem()))
        {
            return;
        }

        Action action = event.getAction();
        if(action.equals(Action.LEFT_CLICK_BLOCK))
        {
            Player player = event.getPlayer();
            Location location = event.getClickedBlock().getLocation();
            event.setCancelled(true);

            plugin.startSyncTask(()->
            {
                Pair<Location, Location> locations = plugin.selectionPoints.get(player.getUniqueId());
                if(locations == null)
                {
                    locations = new Pair<>(location, null);
                }
                else
                {
                    locations = new Pair<>(location, locations.getValue());
                }
                plugin.selectionPoints.put(player.getUniqueId(), locations);
            });

            player.sendMessage(plugin.getMessageManager().ANTISKID_POSITION_1.replace("{COORDINATE}",
                    "(" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")"));
        }
        else if(action.equals(Action.RIGHT_CLICK_BLOCK))
        {
            event.setCancelled(true);

            Player player = event.getPlayer();
            Location location = event.getClickedBlock().getLocation();

            plugin.startSyncTask(()->
            {
                Pair<Location, Location> locations = plugin.selectionPoints.get(player.getUniqueId());
                if(locations == null)
                {
                    locations = new Pair<>(null, location);
                }
                else
                {
                    locations = new Pair<>(locations.getKey(), location);
                }
                plugin.selectionPoints.put(player.getUniqueId(), locations);
            });

            player.sendMessage(plugin.getMessageManager().ANTISKID_POSITION_2.replace("{COORDINATE}",
                    "(" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")"));
        }
    }
}
