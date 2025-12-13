package com.leon.datalink.transform.handler.impl;

import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;

public class WithoutHandler implements TransformHandler {
    @Override
    public void init(Transform transform) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public Object transform(Object data) {
        return data;
    }
}
