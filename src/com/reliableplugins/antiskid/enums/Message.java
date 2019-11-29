/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.enums;

import org.bukkit.ChatColor;

public enum Message
{
    // Info Messages
    FIRST_POINT_SET(ChatColor.GRAY + "position #1: "),
    SECOND_POINT_SET(ChatColor.GRAY + "position #2: "),
    ANTISKID_ON(ChatColor.AQUA + "Antiskid protection has been turned on."),
    ANTISKID_OFF(ChatColor.AQUA + "Antiskid protection has been turned off."),

    // Help Messages
    HELP_TOOL(ChatColor.GRAY + "Left click to set position #1 and right click to set position #2. Type /antiskid on to mask your repeaters"),
    HELP_ANTISKID(ChatColor.RED + "/antiskid <tool/on/off>"),

    // Error Messages
    ERROR_NO_REGION(ChatColor.RED + "You haven't selected a region yet; execute \"/antiskid tool\" and select your region."),
    ERROR_NO_PROTECTED(ChatColor.RED + "You have not turned on antiskid yet."),
    ERROR_NOT_PLAYER(ChatColor.RED + "Only players may execute this command."),
    ERROR_PROTECTED_DIODE(ChatColor.RED + "That diode is protected!");

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