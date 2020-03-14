/*
 * Project: AntiSkid
 * Copyright (C) 2020 Bilal Salha <bsalha1@gmail.com>
 * GNU GPLv3 <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package com.reliableplugins.antiskid.type;

public class Vector<T>
{
    private T x;
    private T y;
    private T z;

    public Vector(T x, T y, T z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(T x)
    {
        this.x = x;
    }

    public void setY(T y)
    {
        this.y = y;
    }

    public void setZ(T z)
    {
        this.z = z;
    }

    public T getX()
    {
        return x;
    }

    public T getY()
    {
        return y;
    }

    public T getZ()
    {
        return z;
    }
}
