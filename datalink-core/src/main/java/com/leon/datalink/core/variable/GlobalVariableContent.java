package com.leon.datalink.core.variable;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 全局变量
 */
public class GlobalVariableContent {

    private static final Map<String, Variable> globalVariable = new HashMap<>();

    static {
        globalVariable.put("version", new Variable("version", "v1.0.0", "系统版本", VariableTypeEnum.SYSTEM));
        globalVariable.put("timeStamp", new Variable("timeStamp", null, "时间戳", VariableTypeEnum.SYSTEM));
        globalVariable.put("date", new Variable("date", null, "日期", VariableTypeEnum.SYSTEM));
        globalVariable.put("dateTime", new Variable("dateTime", null, "日期时间", VariableTypeEnum.SYSTEM));
        globalVariable.put("uuid", new Variable("uuid", null, "UUID", VariableTypeEnum.SYSTEM));
    }

    private static void freshSystemVariable() {
        DateTime now = DateTime.now();
        setValue("timeStamp", now.getTime());
        setValue("date", DateUtil.format(now, "yyyy-MM-dd"));
        setValue("dateTime", DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
        setValue("uuid", UUID.randomUUID().toString());
    }

    public static Variable get(String key) {
        return globalVariable.get(key);
    }

    public static void set(String key, Variable variable) {
        globalVariable.put(key, variable);
    }

    public static Object getValue(String key) {
        return get(key).getValue();
    }

    public static void setValue(String key, Object value) {
        globalVariable.get(key).setValue(value);
    }

    public static Map<String, Variable> getAll() {
        freshSystemVariable();
        return globalVariable;
    }

    public static Map<String, Object> getAllValue() {
        Map<String, Object> result = new HashMap<>();
        for (String key : getAll().keySet()) {
            result.put(key, getValue(key));
        }
        return result;
    }

    public static void remove(String key) {
        globalVariable.remove(key);
    }


}
