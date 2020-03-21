/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.hook;

import com.reliableplugins.antiskid.AntiSkid;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HookManager
{
    private AntiSkid antiSkid;
    private Map<String, PluginHook> pluginMap = new HashMap<>();

    public HookManager(AntiSkid antiSkid)
    {
        this.antiSkid = antiSkid;
        hookPlugin(new FactionHook());
//        hookPlugin(new PlotSquaredHook());
    }

    private void hookPlugin(PluginHook pluginHook)
    {
        if (antiSkid.getServer().getPluginManager().getPlugin(pluginHook.getName()) == null)
        {
            antiSkid.getServer().getLogger().log(Level.SEVERE, "Plugin failed to find " + pluginHook.getName());
            return;
        }
        pluginMap.put(pluginHook.getName().toLowerCase(), (PluginHook<?>) pluginHook.setup(antiSkid));
    }

    public Map<String, PluginHook> getPluginMap()
    {
        return this.pluginMap;
    }

}
