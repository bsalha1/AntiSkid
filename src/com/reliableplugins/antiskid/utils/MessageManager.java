/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.utils;

import com.reliableplugins.antiskid.config.MessageConfig;

public class MessageManager
{
    private MessageConfig config;

    public String HEADER;
    // {NUM}
    public String ANTISKID_ON;

    // {PLAYER}
    public String WHITELIST_ADD;
    public String WHITELIST_REM;
    public String ERROR_PLAYER_NOT_WHITELISTED;
    public String ERROR_PLAYER_ALREADY_WHITELISTED;

    // {LIST}
    public String WHITELIST_LIST;

    public String ANTISKID_RELOAD_START;
    public String ANTISKID_RELOAD_FINISH;
    public String ANTISKID_CLEAR;
    public String ANTISKID_OFF;
    public String HELP_ANTISKID;
    public String HELP_WHITELIST;

    public String ERROR_PROTECTED_DIODE;
    public String ERROR_NO_PERMS;
    public String ERROR_NOT_PLAYER;
    public String ERROR_NOT_PROTECTED;
    public String ERROR_EMPTY_WHITELIST;
    public String ERROR_WHITELIST_SELF;
    public String ERROR_UNWHITELIST_SELF;
    public String ERROR_INVALID_PLAYER;
    public String ERROR_NOT_TERRITORY;
    public String ERROR_NOT_PLOT_OWNER;

    public MessageManager(MessageConfig config)
    {
        this.config = config;
        HEADER = config.getFileConfiguration().getString("message-header");
        ANTISKID_ON = getMessage("antiskid-on");
        WHITELIST_ADD = getMessage("whitelist-add");
        WHITELIST_REM = getMessage("whitelist-remove");
        ERROR_PLAYER_NOT_WHITELISTED = getMessage("err-not-whitelisted");
        ERROR_PLAYER_ALREADY_WHITELISTED = getMessage("err-already-whitelisted");
        WHITELIST_LIST = getMessage("whitelist-list");
        ANTISKID_RELOAD_START = getMessage("antiskid-reload-start");
        ANTISKID_RELOAD_FINISH = getMessage("antiskid-reload-finish");
        ANTISKID_OFF = getMessage("antiskid-off");
        ANTISKID_CLEAR = getMessage("antiskid-clear");
        HELP_ANTISKID = getMessage("antiskid-help");
        HELP_WHITELIST = getMessage("antiskid-whitelist-help");
        ERROR_PROTECTED_DIODE = getMessage("err-protected-diode");
        ERROR_NO_PERMS = getMessage("err-no-perms");
        ERROR_NOT_PLAYER = getMessage("err-not-player");
        ERROR_NOT_PROTECTED = getMessage("err-not-protected");
        ERROR_EMPTY_WHITELIST = getMessage("err-empty-whitelist");
        ERROR_WHITELIST_SELF = getMessage("err-whitelist-self");
        ERROR_UNWHITELIST_SELF = getMessage("err-whitelist-remove-self");
        ERROR_INVALID_PLAYER = getMessage("err-invalid-player");
        ERROR_NOT_TERRITORY = getMessage("err-not-territory");
        ERROR_NOT_PLOT_OWNER = getMessage("err-not-plot-owner");
    }

    private String getMessage(String key)
    {
        String value = config.getFileConfiguration().getString(key);
        return value == null ? "null" : Util.color(HEADER + value);
    }
}

