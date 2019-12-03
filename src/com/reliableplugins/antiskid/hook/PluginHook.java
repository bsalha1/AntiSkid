package com.reliableplugins.antiskid.hook;

import com.reliableplugins.antiskid.Main;

public interface PluginHook<T>
{
    T setup(Main main);

    String getName();
}
