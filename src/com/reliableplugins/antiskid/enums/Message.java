/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.enums;

import org.bukkit.ChatColor;

public enum Message
{
    /* INFO */
    ANTISKID_ON(ChatColor.AQUA + "Antiskid protection has been turned on (%d repeaters protected)"),
    ANTISKID_OFF(ChatColor.AQUA + "Antiskid protection has been turned off."),
    WHITELISTED(ChatColor.AQUA + "%s added to whitelist."),
    UNWHITELISTED(ChatColor.AQUA + "%s removed from whitelist."),
    LIST_WHITELISTED(ChatColor.GRAY + "Whitelist: %s"),

    /* HELP */
    HELP_ANTISKID(ChatColor.RED + "/antiskid <whitelist/on/off> <add/del> <player>"),
    HELP_WHITELIST(ChatColor.RED + "/antiskid whitelist <add/del> <player>"),

    /* ERROR */
    ERROR_NO_PERMS(ChatColor.RED + "You do not have access to this command!"),
    ERROR_NOT_PLAYER(ChatColor.RED + "Only players may execute this command."),

    ERROR_NO_PROTECTED(ChatColor.RED + "You have not turned on antiskid yet."),

    ERROR_NO_WHITELIST(ChatColor.RED + "You have no players in your whitelist."),
    ERROR_PLAYER_NOT_WHITELISTED(ChatColor.RED + "%s is not in your whitelist."),
    ERROR_PLAYER_ALREADY_WHITELISTED(ChatColor.RED + "%s is already in your whitelist."),
    ERROR_INVALID_PLAYER(ChatColor.RED + "Invalid player."),

    ERROR_NOT_TERRITORY(ChatColor.RED + "You can only protect your faction's claims!");


    private final String text;
    private final String header =
            ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "(" +
            ChatColor.BLUE + "" + ChatColor.BOLD + "AntiSkid" +
            ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ")";

    Message(final String text)
    {
        this.text = header + " " + text;
    }

    @Override
    public String toString()
    {
        return text;
    }
}