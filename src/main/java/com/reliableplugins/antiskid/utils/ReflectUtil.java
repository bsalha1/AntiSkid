/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.utils;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

import java.lang.reflect.Field;

public class ReflectUtil
{

    public static <T> T getPrivateField(String fieldName, Object instance) throws NoSuchFieldException, IllegalAccessException
    {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(instance);
    }
}
