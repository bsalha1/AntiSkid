/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.abstracts;

import io.netty.channel.ChannelDuplexHandler;
import org.bukkit.entity.Player;

public abstract class PacketListener extends ChannelDuplexHandler implements Cloneable
{
    protected Player player;

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
