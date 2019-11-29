/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.abstracts;

import com.reliableplugins.antiskid.Main;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public abstract class AbstractItem
{
    protected ItemStack item;
    protected ItemMeta itemMeta;
    protected Main main;
    protected Listener clickHandler;


    /**
     * Creates a basic item
     * @param item
     */
    protected AbstractItem(ItemStack item)
    {
        this.item = item;
        this.itemMeta = item.getItemMeta();
    }


    /**
     * Registers a click handler for the item. Used in plugin initialization
     * @param main
     * @param clickHandler
     */
    protected AbstractItem(Main main, Listener clickHandler)
    {
        this.clickHandler = clickHandler;
        Bukkit.getPluginManager().registerEvents(clickHandler, main);
    }

    public void give(Player player)
    {
        // If inventory full ...
        if(player.getInventory().firstEmpty() == -1)
        {
            return;
        }
        player.getInventory().addItem(this.item);
    }

    public void setEnchants(Map<Enchantment, Integer> enchantments)
    {
        this.item.addUnsafeEnchantments(enchantments);
    }

    public void setDisplayName(String name)
    {
        this.itemMeta.setDisplayName(name);
        this.item.setItemMeta(this.itemMeta);
    }

    public void setLore(List<String> lore)
    {
        this.itemMeta.setLore(lore);
        this.item.setItemMeta(this.itemMeta);
    }

    public ItemStack getItem()
    {
        return this.item;
    }
}
