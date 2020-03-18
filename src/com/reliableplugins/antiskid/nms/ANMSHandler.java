package com.reliableplugins.antiskid.nms;

import com.google.common.base.Function;
import com.reliableplugins.antiskid.type.packet.Packet;
import javafx.util.Pair;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class ANMSHandler implements INMSHandler
{
    private HashMap<Class<? extends Packet>, Function<Pair<Packet, Player>>>
}
