package com.leon.datalink.runtime;

import cn.hutool.core.date.DateTime;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.entity.*;
import com.leon.datalink.runtime.entity.Runtime;

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
    public static void initRuntime(String ruleId, List<String> sourceRuntimeIdList, List<String> destRuntimeIdList, List<String> transformIdList) {
        Runtime ruleRuntime = runtimeList.get(ruleId);

        if (null == ruleRuntime) {
            resetRuntime(ruleId, sourceRuntimeIdList, destRuntimeIdList, transformIdList);
            return;
        }

        // 资源运行状保持
        Map<String, RuntimeEntity> sourceRuntimeMap = new HashMap<>(sourceRuntimeIdList.size());
        Map<String, RuntimeEntity> sourceRuntimeList = ruleRuntime.getSourceRuntimeList();
        for (String id : sourceRuntimeIdList) {
            if (sourceRuntimeList.containsKey(id)) {
                sourceRuntimeMap.put(id, sourceRuntimeList.get(id));
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
                destRuntimeMap.put(id, destRuntimeList.get(id));
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
                transformRuntimeMap.put(id, transformRuntimeList.get(id));
            } else {
                transformRuntimeMap.put(id, new RuntimeEntity());
            }
        }
        ruleRuntime.setTransformRuntimeList(transformRuntimeMap);


    }

    /**
     * 新加运行
     */
    private static Runtime newRuntime(List<String> sourceRuntimeIdList, List<String> destRuntimeIdList, List<String> transformIdList) {
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
        return runtime;
    }

    /**
     * 重置运行时
     * @param ruleId
     * @param sourceRuntimeIdList
     * @param destRuntimeIdList
     * @param transformIdList
     */
    public static void resetRuntime(String ruleId, List<String> sourceRuntimeIdList, List<String> destRuntimeIdList, List<String> transformIdList) {
        runtimeList.put(ruleId, newRuntime(sourceRuntimeIdList, destRuntimeIdList, transformIdList));
    }

    /**
     * 停止运行时 重置全部状态
     * @param ruleId
     */
    public static void stopRuntime(String ruleId) {
        Runtime ruleRuntime = runtimeList.get(ruleId);

        if (null == ruleRuntime) {
            return;
        }
        Map<String, RuntimeEntity> sourceRuntimeList = ruleRuntime.getSourceRuntimeList();
        sourceRuntimeList.values().forEach(RuntimeEntity::initStatus);

        Map<String, RuntimeEntity> transformRuntimeList = ruleRuntime.getTransformRuntimeList();
        transformRuntimeList.values().forEach(RuntimeEntity::initStatus);

        Map<String, RuntimeEntity> destRuntimeList = ruleRuntime.getDestRuntimeList();
        destRuntimeList.values().forEach(RuntimeEntity::initStatus);
    }

    /**
     * 移除运行时
     * @param ruleId
     */
    public static void removeRuntime(String ruleId) {
        runtimeList.remove(ruleId);
    }


    /**
     * 处理数据记录
     * @param ruleId
     * @param runtimeData
     */
    public static void handleRecord(String ruleId, RuntimeData runtimeData) {
        Runtime runtime = runtimeList.get(ruleId);
        if (null == runtime) return;

        RuntimeTypeEnum type = runtimeData.getType();
        String entityRuntimeId = runtimeData.getEntityRuntimeId();

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

    /**
     * 处理状态
     * @param ruleId
     * @param runtimeStatus
     */
    public static void handleStatus(String ruleId, RuntimeStatus runtimeStatus) {
        Runtime runtime = runtimeList.get(ruleId);
        if (null == runtime) return;

        RuntimeTypeEnum type = runtimeStatus.getType();
        String entityRuntimeId = runtimeStatus.getEntityRuntimeId();

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


}
