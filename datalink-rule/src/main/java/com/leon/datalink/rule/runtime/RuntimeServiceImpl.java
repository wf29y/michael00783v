package com.leon.datalink.rule.runtime;

import cn.hutool.core.date.DateTime;
import com.google.common.collect.Lists;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.entity.Runtime;
import com.leon.datalink.rule.runtime.actor.RuntimeUpdateMsg;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuntimeServiceImpl implements RuntimeService {

    /**
     * 规则运行列表
     */
    private final ConcurrentHashMap<String, Runtime> runtimeList = new ConcurrentHashMap<>();

    @Override
    public void init(Rule rule) {
        Runtime runtime = new Runtime();
        runtime.setFailCount(0L);
        runtime.setSuccessCount(0L);
        runtime.setStartTime(DateTime.now());
        runtime.setLastData(Lists.newLinkedList());
        runtimeList.put(rule.getRuleId(), runtime);
    }

    @Override
    public void update(String ruleId, RuntimeUpdateMsg runtimeUpdateMsg) {
        Runtime runtime = runtimeList.get(ruleId);
        runtime.addLastData(runtimeUpdateMsg.getData(), runtimeUpdateMsg.getTime());
        runtime.setLastTime(runtimeUpdateMsg.getTime());
        if (runtimeUpdateMsg.isSuccess()) {
            runtime.addSuccess();
        } else {
            runtime.addFail();
        }
    }

    @Override
    public Runtime get(String ruleId) {
        return runtimeList.get(ruleId);
    }

    @Override
    public void remove(String ruleId) {
        runtimeList.remove(ruleId);
    }
}
