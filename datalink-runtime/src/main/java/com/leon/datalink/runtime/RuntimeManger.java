package com.leon.datalink.runtime;

import cn.hutool.core.date.DateTime;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.runtime.entity.RuntimeStatus;
import com.leon.datalink.runtime.entity.Runtime;
import com.leon.datalink.runtime.entity.RuntimeEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeManger {

    /**
     * 运行时列表
     */
    private static final ConcurrentHashMap<String, Runtime> runtimeList = new ConcurrentHashMap<>();

    /**
     * 初始化
     */
    public static void init(String ruleId, Map<String, Object> initVariables, List<String> sourceRuntimeIdList, List<String> destRuntimeIdList, List<String> transformIdList) {
        Runtime ruleRuntime = runtimeList.get(ruleId);
        // 非首次初始化
        if (null != ruleRuntime) {

            // 同名变量保持运行值
            Map<String, Object> variables = ruleRuntime.getVariables();
            for (String key : variables.keySet()) {
                if (initVariables.containsKey(key)) {
                    initVariables.put(key, variables.get(key));
                }
            }
            ruleRuntime.setVariables(initVariables);

            // 资源运行状保持
            Map<String, RuntimeEntity> sourceRuntimeMap = new HashMap<>(sourceRuntimeIdList.size());
            Map<String, RuntimeEntity> sourceRuntimeList = ruleRuntime.getSourceRuntimeList();
            for (String id : sourceRuntimeIdList) {
                if (sourceRuntimeList.containsKey(id)) {
                    RuntimeEntity runtimeEntity = sourceRuntimeList.get(id);
                    runtimeEntity.setStatus(RuntimeStatusEnum.INIT);
                    sourceRuntimeMap.put(id, runtimeEntity);
                } else {
                    sourceRuntimeMap.put(id, new RuntimeEntity());
                }
            }
            ruleRuntime.setSourceRuntimeList(sourceRuntimeMap);

            // 资源运行状保持
            Map<String, RuntimeEntity> destRuntimeMap = new HashMap<>(destRuntimeIdList.size());
            Map<String, RuntimeEntity> destRuntimeList = ruleRuntime.getDestRuntimeList();
            for (String id : destRuntimeIdList) {
                if (destRuntimeList.containsKey(id)) {
                    RuntimeEntity runtimeEntity = destRuntimeList.get(id);
                    runtimeEntity.setStatus(RuntimeStatusEnum.INIT);
                    destRuntimeMap.put(id, runtimeEntity);
                } else {
                    destRuntimeMap.put(id, new RuntimeEntity());
                }
            }
            ruleRuntime.setDestRuntimeList(destRuntimeMap);

            // 转换运行状保持
            Map<String, RuntimeEntity> transformRuntimeMap = new HashMap<>(transformIdList.size());
            Map<String, RuntimeEntity> transformRuntimeList = ruleRuntime.getTransformRuntimeList();
            for (String id : transformIdList) {
                if (transformRuntimeList.containsKey(id)) {
                    RuntimeEntity runtimeEntity = transformRuntimeList.get(id);
                    runtimeEntity.setStatus(RuntimeStatusEnum.INIT);
                    transformRuntimeMap.put(id, runtimeEntity);
                } else {
                    transformRuntimeMap.put(id, new RuntimeEntity());
                }
            }
            ruleRuntime.setTransformRuntimeList(transformRuntimeMap);

        } else {
            resetRuntime(ruleId, initVariables, sourceRuntimeIdList, destRuntimeIdList, transformIdList);
        }
    }

    /**
     * 新加运行
     */
    private static Runtime newRuntime(Map<String, Object> initVariables, List<String> sourceRuntimeIdList, List<String> destRuntimeIdList, List<String> transformIdList) {
        Map<String, RuntimeEntity> sourceRuntimeMap = new HashMap<>(sourceRuntimeIdList.size());
        for (String id : sourceRuntimeIdList) {
            sourceRuntimeMap.put(id, new RuntimeEntity());
        }
        Map<String, RuntimeEntity> destRuntimeMap = new HashMap<>(destRuntimeIdList.size());
        for (String id : destRuntimeIdList) {
            destRuntimeMap.put(id, new RuntimeEntity());
        }
        Map<String, RuntimeEntity> transformRuntimeMap = new HashMap<>(transformIdList.size());
        for (String id : transformIdList) {
            transformRuntimeMap.put(id, new RuntimeEntity());
        }

        Runtime runtime = new Runtime();
        runtime.setStartTime(DateTime.now());
        runtime.setTransformRuntimeList(transformRuntimeMap);
        runtime.setSourceRuntimeList(sourceRuntimeMap);
        runtime.setDestRuntimeList(destRuntimeMap);
        runtime.setVariables(null == initVariables ? new HashMap<>() : initVariables);
        return runtime;
    }

    public static void resetRuntime(String ruleId, Map<String, Object> initVariables, List<String> sourceRuntimeIdList, List<String> destRuntimeIdList, List<String> transformIdList) {
        runtimeList.put(ruleId, newRuntime(initVariables, sourceRuntimeIdList, destRuntimeIdList, transformIdList));
    }

    public static void handleRecord(String ruleId, String entityRuntimeId, RuntimeData runtimeData) {
        Runtime runtime = runtimeList.get(ruleId);
        if (null == runtime) return;

        RuntimeTypeEnum type = runtimeData.getType();

        switch (type) {
            case SOURCE: {
                Map<String, RuntimeEntity> sourceRuntimeList = runtime.getSourceRuntimeList();
                sourceRuntimeList.get(entityRuntimeId).addDataRecord(runtimeData);
                break;
            }
            case TRANSFORM: {
                Map<String, RuntimeEntity> transformRuntimeList = runtime.getTransformRuntimeList();
                transformRuntimeList.get(entityRuntimeId).addDataRecord(runtimeData);
                break;
            }
            case DEST: {
                Map<String, RuntimeEntity> destRuntimeList = runtime.getDestRuntimeList();
                destRuntimeList.get(entityRuntimeId).addDataRecord(runtimeData);
                break;
            }
        }
    }

    public static void handleStatus(String ruleId, String entityRuntimeId, RuntimeStatus runtimeStatus) {
        Runtime runtime = runtimeList.get(ruleId);
        if (null == runtime) return;

        RuntimeTypeEnum type = runtimeStatus.getType();

        switch (type) {
            case SOURCE: {
                Map<String, RuntimeEntity> sourceRuntimeList = runtime.getSourceRuntimeList();
                sourceRuntimeList.get(entityRuntimeId).updateStatus(runtimeStatus);
                break;
            }
            case TRANSFORM: {
                Map<String, RuntimeEntity> transformRuntimeList = runtime.getTransformRuntimeList();
                transformRuntimeList.get(entityRuntimeId).updateStatus(runtimeStatus);
                break;
            }
            case DEST: {
                Map<String, RuntimeEntity> destRuntimeList = runtime.getDestRuntimeList();
                destRuntimeList.get(entityRuntimeId).updateStatus(runtimeStatus);
                break;
            }
        }
    }

    public static ConcurrentHashMap<String, Runtime> getRuntimeList() {
        return runtimeList;
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
