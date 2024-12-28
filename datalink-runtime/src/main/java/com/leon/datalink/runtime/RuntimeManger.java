package com.leon.datalink.runtime;

import cn.hutool.core.date.DateTime;
import com.google.common.collect.Lists;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;
import com.leon.datalink.runtime.actor.RuntimeUpdateVarMsg;
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
        if (null != runtimeList.get(ruleId)) return;
        Runtime runtime = new Runtime();
        runtime.setTotal(0L);
        runtime.setAnalysisSuccessCount(0L);
        runtime.setAnalysisFailCount(0L);
        runtime.setPublishSuccessCount(0L);
        runtime.setPublishFailCount(0L);
        runtime.setStartTime(DateTime.now());
        runtime.setLastData(Lists.newLinkedList());
        runtime.setVariables(null == initVariables ? new HashMap<>() : initVariables);
        runtimeList.put(ruleId, runtime);
    }

    public static ConcurrentHashMap<String, Runtime> getRuntimeList() {
        return runtimeList;
    }

    public static void updateData(String ruleId, RuntimeUpdateDataMsg msg) {
        Runtime runtime = runtimeList.get(ruleId);
        runtime.addLastData(msg);
        runtime.setLastTime(msg.getTime());
        runtime.addTotalCount();
        if (null != msg.getAnalysisSuccess()) {
            if (msg.getAnalysisSuccess()) {
                runtime.addAnalysisSuccessCount();
                if (null != msg.getPublishSuccess()) {
                    if (msg.getPublishSuccess()) {
                        runtime.addPublishSuccessCount();
                    } else if (!msg.getPublishSuccess()) {
                        runtime.addPublishFailCount();
                    }
                }
            } else if (!msg.getAnalysisSuccess()) {
                runtime.addAnalysisFailCount();
            }
        }

    }

    public static void updateVariable(String ruleId, RuntimeUpdateVarMsg runtimeUpdateVarMsg) {
        Runtime runtime = runtimeList.get(ruleId);
        runtime.setVariables(runtimeUpdateVarMsg.getVariables());
    }

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
