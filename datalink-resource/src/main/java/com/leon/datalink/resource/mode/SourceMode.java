package com.leon.datalink.resource.mode;

import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.constans.SourceModeEnum;

@FunctionalInterface
public interface SourceMode {
    SourceModeEnum get(ConfigProperties configProperties);
}
