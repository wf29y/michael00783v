package com.leon.datalink.rule;

public interface IRuleEngine {

    void start(Rule rule) throws Exception;

    void stop(Rule rule) throws Exception;

}
