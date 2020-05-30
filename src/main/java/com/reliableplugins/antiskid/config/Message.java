package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.utils.BukkitUtil;

public enum Message
{
    HEADER("message-header", "&8(&9AntiSkid&8) "),
    ANTISKID_ON("antiskid-on", "&bAntiskid protection has been turned on ({NUM} chunks protected)."),
    ANTISKID_OFF("antiskid-off", "&bAntiskid protection has been turned off."),
    ANTISKID_CLEAR("antiskid-clear", "&bAntiskid protection has been cleared. {NUM} chunks freed."),
    ANTISKID_RELOAD_START("antiskid-reload-start", "&bAntiskid is reloading."),
    ANTISKID_RELOAD_FINISH("antiskid-reload-finish", "&bAntiskid has reloaded."),

    ANTISKID_POSITION_1("antiskid-position-1", "&bPosition 1 selected at {COORDINATE}"),
    ANTISKID_POSITION_2("antiskid-position-2", "&bPosition 2 selected at {COORDINATE}"),

    WHITELIST_ADD("whitelist-add", "&7{PLAYER} added to whitelist."),
    WHITELIST_REM("whitelist-remove", "&7{PLAYER} removed from whitelist."),
    WHITELIST_LIST("whitelist-list", "&7Whitelist: {LIST}"),

    HELP_ANTISKID("antiskid-help", "&c/antiskid <whitelist/on/off> <add/del> <player>"),
    HELP_WHITELIST("antiskid-whitelist-help", "&c/antiskid whitelist <add/del> <player>"),

    ERROR_NO_PERMS("err-no-perms", "&cYou do not have access to this command!"),
    ERROR_NOT_PLAYER("err-not-player", "&cOnly players may execute this command."),
    ERROR_PROTECTED_DIODE("err-protected-diode", "&cThat repeater is protected!"),
    ERROR_NOT_PROTECTED("err-not-protected", "&cYou have not turned on antiskid yet."),
    ERROR_EMPTY_WHITELIST("err-empty-whitelist", "&cYou have no players in your whitelist."),
    ERROR_PLAYER_NOT_WHITELISTED("err-not-whitelisted", "&c{PLAYER} is not in your whitelist."),
    ERROR_PLAYER_ALREADY_WHITELISTED("err-already-whitelisted", "&c{PLAYER} is already whitelisted."),
    ERROR_INVALID_PLAYER("err-invalid-player", "&cInvalid player."),
    ERROR_WHITELIST_SELF("err-whitelist-self", "&cYou cannot whitelist yourself."),
    ERROR_UNWHITELIST_SELF("err-whitelist-remove-self", "&cYou are not in your whitelist."),
    ERROR_NOT_TERRITORY("err-not-territory", "&cYou can only protect your own faction's claims."),
    ERROR_LOW_RANK("err-low-rank", "&cYou must be at least {RANK} to use AntiSkid."),
    ERROR_NOT_PLOT_OWNER("err-not-plot-owner", "&cYou can only protect your own plot."),
    ERROR_NO_POSITION1("err-no-position-1", "&cYou must left click to create a position 1."),
    ERROR_NO_POSITION2("err-no-position-2", "&cYou must right click to create a position 2."),
    ERROR_ALREADY_PROTECTED("err-already-protected", "&cPart of this region is already protected.");

    String message;
    String configKey;

    Message(String configKey, String message)
    {
        this.configKey = configKey;
        this.message = message;
    }

    public String getRawMessage()
    {
        return message;
    }

    public String getMessage()
    {
        return BukkitUtil.color(message);
    }

    public void setMessage(String message)
    {
        this.message = BukkitUtil.color(message);
    }

    public String getConfigKey()
    {
        return configKey;
    }
}
