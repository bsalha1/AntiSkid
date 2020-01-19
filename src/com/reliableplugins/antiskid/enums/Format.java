/*
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.enums;

public enum Format
{
    PLAYER("{PLAYER}"),
    LIST("{WHITELIST}"),
    NUM("{NUM}");

    private final String format;

    Format(String format)
    {
        this.format = format;
    }

    @Override
    public String toString()
    {
        return format;
    }
}
