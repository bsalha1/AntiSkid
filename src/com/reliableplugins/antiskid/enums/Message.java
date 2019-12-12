/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.enums;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.annotation.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Message
{
    // {NUM}
    @MessageFormat(formats = Format.NUM)
    ANTISKID_ON("antiskid-on"),

    // {PLAYER}
    @MessageFormat(formats = Format.PLAYER)
    WHITELIST_ADD("whitelist-add"),

    @MessageFormat(formats = Format.PLAYER)
    WHITELIST_REM("whitelist-remove"),

    @MessageFormat(formats = Format.PLAYER)
    ERROR_PLAYER_NOT_WHITELISTED("err-not-whitelisted"),

    @MessageFormat(formats = Format.PLAYER)
    ERROR_PLAYER_ALREADY_WHITELISTED("err-already-whitelisted"),

    // {LIST}
    @MessageFormat(formats = Format.WHITELIST)
    WHITELIST_LIST("whitelist-list"),

    ANTISKID_OFF("antiskid-off"),
    HELP_ANTISKID("antiskid-help"),
    HELP_WHITELIST("antiskid-whitelist-help"),
    ERROR_NO_PERMS("err-no-perms"),
    ERROR_NOT_PLAYER("err-not-player"),
    ERROR_NOT_PROTECTED("err-not-protected"),
    ERROR_EMPTY_WHITELIST("err-empty-whitelist"),
    ERROR_WHITELIST_SELF("err-whitelist-self"),
    ERROR_UNWHITELIST_SELF("err-whitelist-remove-self"),
    ERROR_INVALID_PLAYER("err-invalid-player"),
    ERROR_NOT_TERRITORY("err-not-territory");

    private final String text;
    private final String header;
    private FileConfiguration config = AntiSkid.mainConfig.getFileConfiguration();

    private String getMessage(String key)
    {
        return ChatColor.translateAlternateColorCodes('&', config.getString(key));
    }

    Message(final String key)
    {
        this.header = getMessage("message-header");
        this.text = getMessage(key);
    }

    @Override
    public String toString()
    {
        return header + text;
    }
}

