package com.leon.datalink.resource.driver;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.util.CharsetUtil;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Tailer;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileDriver extends AbstractDriver {

    private Tailer tailer;

    private final ConcurrentHashMap<String, FileAppender> appenderList = new ConcurrentHashMap<>();

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        String path = properties.getString("path");
        if (null == path) throw new ValidateException();

        if (DriverModeEnum.SOURCE.equals(driverMode)) {
            String file = properties.getString("file");
            if (null == file) throw new ValidateException();

            Long delay = properties.getLong("delay", 1000);

            File destFile = new File(path, file);
            if (!FileUtil.isFile(destFile)) {
                produceDataError("文件不存在");
                return;
            }
            tailer = new Tailer(destFile, CharsetUtil.CHARSET_UTF_8, content -> {
                if (StringUtils.isEmpty(content)) return;
                Map<String, Object> result = new HashMap<>();
                result.put("file", destFile.getPath());
                result.put("content", content);
                produceData(result);
            }, 0, delay);
            tailer.start(true);
        }
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {

        Map<String, Object> variable = getVariable(data);
        // 路径模板解析
        String file = properties.getString("file");
        if (!StringUtils.isEmpty(file)) {
            String render = this.templateAnalysis(file, variable);
            if (!StringUtils.isEmpty(render)) file = render;
        }

        FileAppender fileAppender = appenderList.get(file);
        if (null == fileAppender) {
            String path = properties.getString("path");
            Integer buffer = properties.getInteger("buffer", 5);
            boolean lineMode = properties.getBoolean("lineMode", true);
            fileAppender = new FileAppender(new File(path, file), buffer, lineMode);
            appenderList.put(file, fileAppender);
        }

        // 模板解析
        String content = properties.getString("content");
        if (!StringUtils.isEmpty(content)) {
            String render = this.templateAnalysis(content, variable);
            if (!StringUtils.isEmpty(render)) content = render;
        } else {
            if (null != data) {
                content = JacksonUtils.toJson(data);
            }
        }
        fileAppender.append(content);

        return content;
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (DriverModeEnum.DEST.equals(driverMode)) {
            if (CollectionUtil.isNotEmpty(appenderList)) {
                appenderList.forEach((key, value) -> value.flush());
            }
        } else {
            if (null != tailer) {
                tailer.stop();
            }
        }
    }

    @Override
    public boolean test(ConfigProperties properties) {
        String path = properties.getString("path");
        if (null == path) return false;
        return FileUtil.isDirectory(path);
    }

}
