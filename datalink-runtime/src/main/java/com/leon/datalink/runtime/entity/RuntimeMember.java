package com.leon.datalink.runtime.entity;

import com.leon.datalink.core.serializer.ProtostuffSerializable;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;

/**
 * 集群下每个节点的运行时
 */
public class RuntimeMember implements ProtostuffSerializable {

    private RuntimeStatusEnum status;

    private String message;

    private String memberName;

    // 成功统计
    private long successCount;

    // 失败统计
    private long failCount;

    public RuntimeMember() {
    }

    public RuntimeMember(String memberName) {
        this.status = RuntimeStatusEnum.INIT;
        this.memberName = memberName;
    }

    public void addDataRecord(RuntimeData runtimeData) {
        if (runtimeData.isError()) {
            failCount++;
        } else {
            successCount++;
        }
    }

    public void updateStatus(RuntimeStatus runtimeStatus) {
        this.status = runtimeStatus.getStatus();
        this.message = runtimeStatus.getMessage();
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

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
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

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }
}
