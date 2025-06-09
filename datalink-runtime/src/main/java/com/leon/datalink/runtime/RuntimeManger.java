package com.leon.datalink.runtime;

import cn.hutool.core.date.DateTime;
import com.google.common.collect.Lists;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;
//import com.leon.datalink.runtime.actor.RuntimeUpdateVarMsg;
import com.leon.datalink.runtime.entity.Runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeManger {

    /**
     * 运行时列表
     */
    private static final ConcurrentHashMap<String, Runtime> runtimeList = new ConcurrentHashMap<>();


    public static void init(String ruleId, Map<String, Object> initVariables) {
        Runtime ruleRuntime = runtimeList.get(ruleId);
        // 重新启动
        if (null != ruleRuntime) {
            Map<String, Object> variables = ruleRuntime.getVariables();
            // 同名变量保持运行值
            for (String key : variables.keySet()) {
                if (initVariables.containsKey(key)) {
                    initVariables.put(key, variables.get(key));
                }
            }
            ruleRuntime.setVariables(initVariables);
        } else {
            runtimeList.put(ruleId, newRuntime(initVariables));
        }
    }

    private static Runtime newRuntime(Map<String, Object> initVariables) {
        Runtime runtime = new Runtime();
        runtime.setTotal(0L);
        runtime.setTransformSuccessCount(0L);
        runtime.setTransformFailCount(0L);
        runtime.setPublishSuccessCount(0L);
        runtime.setPublishFailCount(0L);
        runtime.setStartTime(DateTime.now());
        runtime.setLastData(Lists.newLinkedList());
        runtime.setVariables(null == initVariables ? new HashMap<>() : initVariables);
        return runtime;
    }

    public static void resetRuntime(String ruleId, Map<String, Object> initVariables) {
        runtimeList.put(ruleId, newRuntime(initVariables));
    }

    public static ConcurrentHashMap<String, Runtime> getRuntimeList() {
        return runtimeList;
    }

    public static void updateData(String ruleId, RuntimeUpdateDataMsg msg) {
        Runtime runtime = runtimeList.get(ruleId);
        runtime.addLastData(msg);
        runtime.setLastTime(msg.getTime());
        runtime.addTotalCount();
        if (null != msg.getTransformSuccess()) {
            if (msg.getTransformSuccess()) {
                runtime.addTransformSuccessCount();
                if (null != msg.getPublishSuccess()) {
                    if (msg.getPublishSuccess()) {
                        runtime.addPublishSuccessCount();
                    } else if (!msg.getPublishSuccess()) {
                        runtime.addPublishFailCount();
                    }
                }
            } else if (!msg.getTransformSuccess()) {
                runtime.addTransformFailCount();
            }
        }

    }

//    public static void updateVariable(String ruleId, RuntimeUpdateVarMsg runtimeUpdateVarMsg) {
//        Runtime runtime = runtimeList.get(ruleId);
//        runtime.setVariables(runtimeUpdateVarMsg.getVariables());
//    }

    public static void setRuntime(String ruleId, Runtime runtime) {
        runtimeList.put(ruleId, runtime);
    }

    public static Runtime getRuntime(String ruleId) {
        return runtimeList.get(ruleId);
    }


    public static Map<String, Object> getVariables(String ruleId) {
        return runtimeList.get(ruleId).getVariables();
    }

    public static void removeRuntime(String ruleId) {
        runtimeList.remove(ruleId);
    }

}
