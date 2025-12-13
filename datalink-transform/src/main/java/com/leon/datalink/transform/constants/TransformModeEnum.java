package com.leon.datalink.transform.constants;

import com.leon.datalink.transform.handler.TransformHandler;
import com.leon.datalink.transform.handler.impl.PluginHandler;
import com.leon.datalink.transform.handler.impl.ScriptHandler;
import com.leon.datalink.transform.handler.impl.SqlHandler;
import com.leon.datalink.transform.handler.impl.WithoutHandler;

public enum TransformModeEnum {

    WITHOUT(WithoutHandler.class),
    SQL(SqlHandler.class),
    SCRIPT(ScriptHandler.class),
    PLUGIN(PluginHandler.class);

    private final Class<? extends TransformHandler> transformHandler;

    TransformModeEnum(Class<? extends TransformHandler> transformHandler) {
        this.transformHandler = transformHandler;
    }
    public Class<? extends TransformHandler> getTransformHandler() {
        return transformHandler;
    }

}
