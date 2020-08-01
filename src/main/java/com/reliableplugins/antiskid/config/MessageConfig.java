package com.reliableplugins.antiskid.config;

import com.reliableplugins.antiskid.utils.BukkitUtil;

public class MessageConfig extends Config
{
    public MessageConfig()
    {
        super("messages.yml");
    }

    @Override
    public void load()
    {
        for(Message message : Message.values())
        {
            message.setMessage(getString(BukkitUtil.color(message.getConfigKey()),
                    message.getLoneMessage().replace("§", "&")));
        }

        save();
    }
}
