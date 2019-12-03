/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.listeners;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.enums.Message;
import com.reliableplugins.antiskid.items.AntiSkidTool;
import javafx.util.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AntiSkidToolHandler implements Listener
{
    private Main main;

    public AntiSkidToolHandler(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onAntiSkidToolUse(PlayerInteractEvent event)
    {
        Action action = event.getAction();

        // If item is not the antiskid tool or the action isnt left click or right click block, exit
        if(!event.getPlayer().getItemInHand().equals(new AntiSkidTool().getItem())
                || (!action.equals(Action.LEFT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_BLOCK))) return;

        event.setCancelled(true); // Don't let item till the ground

        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
        Pair<Location, Location> points = null;

        /* CHANGE POSITION 1 */
        if(action.equals(Action.LEFT_CLICK_BLOCK))
        {
            // If player doesn't have any toolpoints, add a toolpoint and make position 2 null
            if(!main.toolPoints.containsKey(player)) points = new Pair<>(location, null);

            // If player already has a toolpoint, keep position 2 but change position 1
            else points = new Pair<>(location, main.toolPoints.get(player).getValue());

            player.sendMessage(String.format(Message.FIRST_POINT_SET.toString(), location.toVector().toString()));
        }

        /* CHANGE POSITION 2 */
        if(action.equals(Action.RIGHT_CLICK_BLOCK))
        {
            // If player doesn't have any toolpoints, add a toolpoint and make position 1 null
            if(!main.toolPoints.containsKey(player)) points = new Pair<>(null, location);

            // If player already has a toolpoint, keep position 1 but change position 2
            else points = new Pair<>(main.toolPoints.get(player).getKey(), location);

            player.sendMessage(String.format(Message.SECOND_POINT_SET.toString(), location.toVector().toString()));
        }

        main.toolPoints.put(player, points);
    }
}
