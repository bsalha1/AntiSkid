/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.items;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.abstracts.AbstractItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class AntiSkidTool extends AbstractItem
{
    private static final ItemStack ITEM = new ItemStack(Material.GOLD_HOE);

    /**
     * Loads the clickhandler
     * @param antiSkid
     * @param clickHandler
     */
    public AntiSkidTool(AntiSkid antiSkid, Listener clickHandler)
    {
        super(antiSkid, clickHandler);
    }



    /**
     * Basic item initialization
     */
    public AntiSkidTool()
    {
        super(ITEM);
        this.setDisplayName(ChatColor.BLUE + "AntiSkid Protector");
    }
}
