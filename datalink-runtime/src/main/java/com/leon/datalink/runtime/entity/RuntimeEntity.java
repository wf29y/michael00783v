package com.leon.datalink.runtime.entity;

import com.google.common.collect.Lists;
import com.leon.datalink.runtime.constants.Constants;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;

import java.io.Serializable;
import java.util.LinkedList;

public class RuntimeEntity implements Serializable {
    private static final long serialVersionUID = 1895166087085804777L;

    // 当前状态（初始化、正常、异常）
    private RuntimeStatusEnum status;

    // 异常信息
    private String message;

    // 成功统计
    private long successCount;

    // 失败统计
    private long failCount;

    private final LinkedList<RuntimeData> runtimeDataList;

    public RuntimeEntity() {
        status = RuntimeStatusEnum.INIT;
        runtimeDataList = Lists.newLinkedList();
    }

    public void addDataRecord(RuntimeData runtimeData) {
        this.runtimeDataList.addFirst(runtimeData);
        if (this.runtimeDataList.size() > Constants.DATA_RECORD_LIMIT) {
            this.runtimeDataList.removeLast();
        }
        if (runtimeData.isError()) {
            failCount++;
            status = RuntimeStatusEnum.ABNORMAL;
            message = runtimeData.getErrorMessage();
        } else {
            successCount++;
            status = RuntimeStatusEnum.NORMAL;
        }
    }

    public void updateStatus(RuntimeStatus runtimeStatus) {
        this.status = runtimeStatus.getStatus();
        this.message = runtimeStatus.getErrorMessage();
    }

    public RuntimeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RuntimeStatusEnum status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LinkedList<RuntimeData> getRuntimeDataList() {
        return runtimeDataList;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failedCount) {
        this.failCount = failedCount;
    }

}
