package com.reliableplugins.antiskid;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils
{
    public static Player me = Bukkit.getPlayer("ReachCarter");

    public static void msg(String message)
    {
        me.sendMessage(message);
    }
}
