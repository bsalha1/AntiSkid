package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.config.Message;
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

            AntiSkid.INSTANCE.startSynchronousTask(()->
            {
                Pair<Location, Location> locations = AntiSkid.INSTANCE.selectionPoints.get(player.getUniqueId());
                if(locations == null)
                {
                    locations = new Pair<>(location, null);
                }
                else
                {
                    locations = new Pair<>(location, locations.getValue());
                }
                AntiSkid.INSTANCE.selectionPoints.put(player.getUniqueId(), locations);
            });

            player.sendMessage(Message.ANTISKID_POSITION_1.getMessage().replace("{COORDINATE}",
                    "(" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")"));
        }
        else if(action.equals(Action.RIGHT_CLICK_BLOCK))
        {
            event.setCancelled(true);

            Player player = event.getPlayer();
            Location location = event.getClickedBlock().getLocation();

            AntiSkid.INSTANCE.startSynchronousTask(()->
            {
                Pair<Location, Location> locations = AntiSkid.INSTANCE.selectionPoints.get(player.getUniqueId());
                if(locations == null)
                {
                    locations = new Pair<>(null, location);
                }
                else
                {
                    locations = new Pair<>(locations.getKey(), location);
                }
                AntiSkid.INSTANCE.selectionPoints.put(player.getUniqueId(), locations);
            });

            player.sendMessage(Message.ANTISKID_POSITION_2.getMessage().replace("{COORDINATE}",
                    "(" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")"));
        }
    }
}
