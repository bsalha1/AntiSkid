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
    ANTISKID_ON(ChatColor.GREEN + "Antiskid protection has been turned on."),
    ANTISKID_OFF(ChatColor.GREEN + "Antiskid protection has been turned off."),

    // Help Messages
    HELP_ANTISKID(ChatColor.RED + "/antiskid on/off"),

    // Error Messages
    ERROR_NOT_PLAYER(ChatColor.RED + "Only players may execute this command."),
    ERROR_PROTECTED_DIODE(ChatColor.RED + "That diode is protected!"),
    ERROR_ALREADY_PROTECTED(ChatColor.RED + "You already have antiskid on."),
    ERROR_NOT_PROTECTED(ChatColor.RED + "You do not have antiskid on.");

    private final String text;
    Message(final String text)
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return text;
    }
}