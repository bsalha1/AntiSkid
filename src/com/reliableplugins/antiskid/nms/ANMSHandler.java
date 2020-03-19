package com.reliableplugins.antiskid.nms;

import com.reliableplugins.antiskid.type.packet.Packet;
import javafx.util.Pair;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Function;

public abstract class ANMSHandler implements INMSHandler
{
    protected HashMap<Class, Function<Pair<Object, Player>, Packet>> packetWrapper;

    public final Packet getPacket(Object packet, Player player)
    {
        if(packet == null || player == null)
        {
            return null;
        }

        Function<Pair<Object, Player>, Packet> wrapper = packetWrapper.get(packet.getClass());
        if(wrapper != null)
        {
            return wrapper.apply(new Pair<>(packet, player));
        }
        return null;
    }
}
