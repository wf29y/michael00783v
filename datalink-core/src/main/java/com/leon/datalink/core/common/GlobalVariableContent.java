package com.leon.datalink.core.common;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 全局变量
 */
public class GlobalVariableContent {

    private static final Map<String, Object> globalVariable = new HashMap<>();

    static {
        globalVariable.put("version", "1.0.0");
    }

    public static Map<String, Object> get() {
        DateTime now = DateTime.now();
        globalVariable.put("timeStamp",now.getTime());
        globalVariable.put("date", DateUtil.format(now,"yyyy-MM-dd"));
        globalVariable.put("dateTime", DateUtil.format(now,"yyyy-MM-dd HH:mm:ss"));
        globalVariable.put("uuid", UUID.randomUUID().toString());
        return globalVariable;
    }

}
