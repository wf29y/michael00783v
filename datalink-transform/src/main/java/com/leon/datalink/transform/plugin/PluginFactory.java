package com.leon.datalink.transform.plugin;

import com.leon.datalink.core.utils.ClassUtil;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.plugin.DataLinkTransformPlugin;
import org.reflections.Reflections;

import java.util.Set;


/**
 * 插件
 */
public class PluginFactory {

    public static DataLinkTransformPlugin createTransformPlugin(String jarPath, String packagePath) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (StringUtils.isEmpty(jarPath) || StringUtils.isEmpty(packagePath)) return null;
        ClassUtil.loadJar(jarPath);
        Reflections reflections = new Reflections(packagePath);
        Set<Class<? extends DataLinkTransformPlugin>> set = reflections.getSubTypesOf(DataLinkTransformPlugin.class);
        if (!set.isEmpty()) {
            return set.stream().findFirst().get().newInstance();
        }
        return null;
    }

}
