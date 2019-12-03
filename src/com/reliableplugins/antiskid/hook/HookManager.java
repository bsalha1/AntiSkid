/*******************************************************************************
 * Project: AntiSkid
 * Copyright (C) 2019 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 ******************************************************************************/

package com.reliableplugins.antiskid.hook;

import com.reliableplugins.antiskid.Main;
import com.reliableplugins.antiskid.hook.impl.FactionHook;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HookManager
{
    private Main main;
    private Map<String, PluginHook> pluginMap = new HashMap<>();

    public HookManager(Main main)
    {
        this.main = main;
        hookPlugin(new FactionHook());
    }

    private void hookPlugin(PluginHook pluginHook)
    {
        if (main.getServer().getPluginManager().getPlugin(pluginHook.getName()) == null)
        {
            main.getServer().getLogger().log(Level.SEVERE, "Plugin failed to find " + pluginHook.getName());
            return;
        }
        pluginMap.put(pluginHook.getName().toLowerCase(), (PluginHook<?>) pluginHook.setup(main));
    }

    public Map<String, PluginHook> getPluginMap()
    {
        return this.pluginMap;
    }

}
