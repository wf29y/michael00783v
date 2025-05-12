
package com.leon.datalink.rule.transform;

import java.lang.reflect.Constructor;


/**
 * TransformFactory
 *
 * @author leon
 */
public class TransformHandlerFactory {

    public static TransformHandler getHandler(Class<? extends TransformHandler> handlerClass) throws Exception {
        if (handlerClass == null) return null;
        Constructor<? extends TransformHandler> constructor = handlerClass.getDeclaredConstructor();
        return constructor.newInstance();
    }

}
