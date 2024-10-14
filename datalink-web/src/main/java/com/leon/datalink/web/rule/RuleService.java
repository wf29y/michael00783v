package com.leon.datalink.web.rule;

import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.entity.Runtime;

public interface RuleService extends BaseService<Rule> {

    Runtime getRuntime(String ruleId);

    void startRule(Rule rule) throws Exception;

    void stopRule(Rule rule) throws Exception;


}
