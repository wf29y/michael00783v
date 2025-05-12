package com.leon.datalink.rule.transform.impl;

import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.transform.TransformHandler;

public class WithoutHandler implements TransformHandler {
    @Override
    public void init(Rule rule) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public Object transform(Object data) {
        return data;
    }
}
