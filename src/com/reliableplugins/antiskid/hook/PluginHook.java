package com.reliableplugins.antiskid.hook;

import com.reliableplugins.antiskid.AntiSkid;

public interface PluginHook<T>
{
    T setup(AntiSkid antiSkid);

    String getName();
}
