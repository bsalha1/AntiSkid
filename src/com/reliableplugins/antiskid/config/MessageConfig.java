/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.AntiSkid;

public class MessageConfig extends Config
{
    public MessageConfig(AntiSkid plugin, String fileName)
    {
        super(plugin, fileName);
        addDefault("message-header", "&8(&9AntiSkid&8) ");
        addDefault("antiskid-on", "&bAntiskid protection has been turned on ({NUM} chunks protected).");
        addDefault("antiskid-off", "&bAntiskid protection has been turned off.");
        addDefault("antiskid-clear", "&bAntiskid protection has been cleared. {NUM} chunks freed.");
        addDefault("antiskid-reload-start", "&bAntiskid is reloading.");
        addDefault("antiskid-reload-finish", "&bAntiskid has reloaded.");

        addDefault("antiskid-position-1", "&bPosition 1 selected at {COORDINATE}");
        addDefault("antiskid-position-2", "&bPosition 2 selected at {COORDINATE}");

        addDefault("whitelist-add", "&7{PLAYER} added to whitelist.");
        addDefault("whitelist-remove", "&7{PLAYER} removed from whitelist.");
        addDefault("whitelist-list", "&7Whitelist: {LIST}");

        addDefault("antiskid-help", "&c/antiskid <whitelist/on/off> <add/del> <player>");
        addDefault("antiskid-whitelist-help", "&c/antiskid whitelist <add/del> <player>");

        addDefault("err-no-perms", "&cYou do not have access to this command!");
        addDefault("err-not-player", "&cOnly players may execute this command.");
        addDefault("err-protected-diode", "&cThat repeater is protected!");
        addDefault("err-not-protected", "&cYou have not turned on antiskid yet.");
        addDefault("err-empty-whitelist", "&cYou have no players in your whitelist.");
        addDefault("err-not-whitelisted", "&c{PLAYER} is not in your whitelist.");
        addDefault("err-already-whitelisted", "&c{PLAYER} is already whitelisted.");
        addDefault("err-invalid-player", "&cInvalid player.");
        addDefault("err-whitelist-self", "&cYou cannot whitelist yourself.");
        addDefault("err-whitelist-remove-self", "&cYou are not in your whitelist.");
        addDefault("err-not-territory", "&cYou can only protect your own faction's claims.");
        addDefault("err-low-rank", "&cYou must be at least {RANK} to use AntiSkid.");
//        addDefault("err-not-plot-owner", "&cYou can only protect your own plot.");
        addDefault("err-no-position-1", "&cYou must left click to create a position 1.");
        addDefault("err-no-position-2", "&cYou must right click to create a position 2.");
        addDefault("err-already-protected", "&cPart of this region is already protected.");

        loadDefaults();
    }
}
