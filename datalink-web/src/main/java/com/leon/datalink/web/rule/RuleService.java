package com.leon.datalink.web.rule;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.rule.Rule;

import java.util.List;

public interface RuleService {

    Rule getRule(String ruleId);

    void addRule(Rule rule) throws KvStorageException;

    void updateRule(Rule rule) throws KvStorageException;

    void removeRule(Rule rule) throws KvStorageException;

    List<Rule> listRule(Rule rule);

    int getRuleCount(Rule rule);

    void startRule(Rule rule) throws Exception;

    void stopRule(Rule rule) throws Exception;


}
