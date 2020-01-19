package com.reliableplugins.antiskid.nms;

import com.reliableplugins.antiskid.AntiSkid;
import com.reliableplugins.antiskid.nms.impl.*;

public class NMSManager
{
    private AntiSkid plugin;

    public NMSManager(AntiSkid plugin)
    {
        this.plugin = plugin;
        switch (getVersion())
        {
            case "v1_11_R1":
                addVersion(new Version_1_11_R1());
                break;
            case "v1_12_R1":
                addVersion(new Version_1_12_R1());
                break;
            case "v1_13_R1":
                addVersion(new Version_1_13_R1());
                break;
            case "v1_13_R2":
                addVersion(new Version_1_13_R2());
                break;
            case "v1_14_R1":
                addVersion(new Version_1_14_R1());
                break;
            default:
                addVersion(new Version_1_8_R3());
        }
    }

    private String getVersion()
    {
        return plugin.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    private void addVersion(INMSHandler nms)
    {
        plugin.setNMS(nms);
    }

}