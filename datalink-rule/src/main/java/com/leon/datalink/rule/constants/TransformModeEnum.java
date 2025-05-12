package com.leon.datalink.rule.constants;

import com.leon.datalink.rule.transform.TransformHandler;
import com.leon.datalink.rule.transform.impl.PluginHandler;
import com.leon.datalink.rule.transform.impl.ScriptHandler;
import com.leon.datalink.rule.transform.impl.WithoutHandler;

public enum TransformModeEnum {

    WITHOUT(WithoutHandler.class),
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
