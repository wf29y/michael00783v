package com.leon.datalink.web.model;

/**
 * @ClassNameSystemInfo
 * @Description
 * @Author Leon
 * @Date2022/4/11 10:35
 * @Version V1.0
 **/
public class SystemInfo {

    /**
     * 资源数量
     */
    private int resourceCount;

    /**
     * 规则数量
     */
    private int ruleCount;

    public int getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

    public int getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(int ruleCount) {
        this.ruleCount = ruleCount;
    }
}
