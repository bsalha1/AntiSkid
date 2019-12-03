/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.abstracts;

import org.bukkit.command.CommandExecutor;

public abstract class AbstractCommand implements CommandExecutor
{
    private String node;

    public String getNode()
    {
        return this.node;
    }

    public void setNode(String node)
    {
        this.node = node;
    }
}
