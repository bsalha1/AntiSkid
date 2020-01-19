/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.hook;

import com.reliableplugins.antiskid.AntiSkid;

public interface PluginHook<T>
{
    T setup(AntiSkid antiSkid);

    String getName();
}
