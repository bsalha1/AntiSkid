package com.reliableplugins.antiskid.type;

import com.reliableplugins.antiskid.utils.Util;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class SelectionTool
{

    public static ItemStack getItem()
    {
        ItemStack item = new ItemStack(Material.DIAMOND_HOE, 1);
        List<String> lore = new LinkedList<>();
        lore.add(Util.color("&eSelect the two corners of the region you want to protect"));
        lore.add(Util.color("&7Left click for corner 1, right click for corner 2"));

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Util.color("&eAntiSkid Selection Tool"));
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}
