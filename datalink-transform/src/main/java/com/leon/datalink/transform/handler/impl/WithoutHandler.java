package com.leon.datalink.transform.handler.impl;

import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;

import java.util.function.Consumer;

public class WithoutHandler implements TransformHandler {
    @Override
    public void init(Transform transform) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void transform(RuntimeData runtimeData, Consumer<Object> consumer) {
        consumer.accept(runtimeData.getData());
    }
}
