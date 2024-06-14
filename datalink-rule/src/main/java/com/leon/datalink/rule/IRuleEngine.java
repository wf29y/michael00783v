package com.leon.datalink.rule;

import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.entity.RuleRuntime;

public interface IRuleEngine {

    RuleRuntime getRuntime(String ruleId);

    void start(Rule rule) throws Exception;

    void stop(String ruleId) throws Exception;

}
