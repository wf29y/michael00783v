package com.leon.datalink.web.rule;

import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.rule.entity.Rule;

public interface RuleService extends BaseService<Rule> {

    void startRule(Rule rule) throws Exception;

    void stopRule(Rule rule) throws Exception;

}
