package com.leon.datalink.transform.handler;


import com.leon.datalink.transform.Transform;

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
     * @param data
     * @return
     */
    Object transform(Object data);

}
