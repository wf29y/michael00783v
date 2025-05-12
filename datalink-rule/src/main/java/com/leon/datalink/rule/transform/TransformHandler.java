package com.leon.datalink.rule.transform;

import com.leon.datalink.rule.entity.Rule;

public interface TransformHandler {

    /**
     * handler init
     * @param rule
     */
    void init(Rule rule);

    /**
     * handler destroy
     */
    void destroy();

    /**
     * handle data
     * @param data
     * @return
     */
    Object transform(Object data);

}
