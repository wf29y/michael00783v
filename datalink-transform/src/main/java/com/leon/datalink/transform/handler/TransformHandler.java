package com.leon.datalink.transform.handler;


import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;

import java.util.function.Consumer;

public interface TransformHandler {

    /**
     * handler init
     * @param transform
     */
    void init(Transform transform);

    /**
     * handler destroy
     */
    void destroy();

    /**
     * handle data
     * @param runtimeData
     * @return
     */
    void transform(RuntimeData runtimeData, Consumer<Object> consumer);

}
