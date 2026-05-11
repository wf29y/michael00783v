package com.leon.datalink.core.utils;

public class EnvUtil {

    public static boolean isCluster(){
        return Boolean.parseBoolean(System.getProperty("datalink.cluster"));
    }

}
