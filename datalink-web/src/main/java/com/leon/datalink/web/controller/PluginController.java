package com.leon.datalink.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.utils.DiskUtils;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.rule.entity.Plugin;
import com.leon.datalink.rule.entity.Script;
import com.leon.datalink.web.config.NotWrap;
import com.leon.datalink.web.plugin.PluginService;
import com.leon.datalink.web.script.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.script.*;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RulesController
 * @Description 插件管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/plugin")
public class PluginController {

    @Autowired
    private PluginService pluginService;

    /**
     * 上传jar文件
     *
     * @param jarFile
     * @throws IOException
     */
    @PostMapping(value = "/upload", consumes = "multipart/*", headers = "Content-Type=multipart/form-data")
    public Object upload(@RequestParam("file") MultipartFile jarFile) throws IOException, KvStorageException {
        if (jarFile == null || jarFile.getOriginalFilename() == null) {
            return null;
        }
        String pluginName = jarFile.getOriginalFilename();
        pluginService.upload(pluginName, jarFile.getBytes());

        HashMap<String, String> result = new HashMap<>();
        result.put("name", pluginName);
        return result;
    }



    /**
     * 下载插件文件
     */
    @GetMapping(value = "/download")
    public void download(@RequestParam("pluginName") String pluginName, HttpServletResponse response) throws IOException, KvStorageException {
        response.setHeader("Content-Disposition", "attachment;filename=" + pluginName);
        response.setContentType("application/octet-stream");
        OutputStream out = response.getOutputStream();
        out.write(pluginService.download(pluginName));
        out.flush();
    }

    /**
     * 查询插件
     *
     * @param pluginId
     * @throws KvStorageException
     */
    @GetMapping("/info")
    public Object getPlugin(@RequestParam(value = "pluginId") String pluginId) {
        return pluginService.get(pluginId);
    }


    /**
     * 新增插件
     *
     * @param plugin
     * @throws KvStorageException
     */
    @PostMapping("/add")
    public void addPlugin(@RequestBody Plugin plugin) throws Exception {
        plugin.setUpdateTime(DateUtil.now());
        pluginService.add(plugin);
    }

    /**
     * 查询插件
     *
     * @param plugin
     */
    @PostMapping("/list")
    public Object listPlugin(@RequestBody Plugin plugin) {
        return pluginService.list(plugin);
    }

    /**
     * 移除插件
     *
     * @param plugin
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removePlugin(@RequestBody Plugin plugin) throws Exception {
        pluginService.remove(plugin);
    }

    /**
     * 更新插件
     *
     * @param plugin
     * @throws Exception
     */
    @PutMapping("/update")
    public void updatePlugin(@RequestBody Plugin plugin) throws Exception {
        plugin.setUpdateTime(DateUtil.now());
        pluginService.update(plugin);
    }

}

