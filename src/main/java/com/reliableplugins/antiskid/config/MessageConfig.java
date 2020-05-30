package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.utils.BukkitUtil;

public class MessageConfig extends Config
{
    public MessageConfig(AntiSkid plugin)
    {
        super(plugin, "messages.yml");
    }

    @Override
    public void load()
    {
        for(Message message : Message.values())
        {
            message.setMessage(getString(BukkitUtil.color(message.getConfigKey()),
                    message.getMessage().replace("§", "&")));
        }

        save();
    }
}
