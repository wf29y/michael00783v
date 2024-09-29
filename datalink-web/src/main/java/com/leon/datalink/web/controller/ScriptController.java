package com.leon.datalink.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.rule.script.Script;
import com.leon.datalink.web.script.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RulesController
 * @Description 脚本管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/script")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    /**
     * 新增脚本
     *
     * @param scriptId
     * @throws KvStorageException
     */
    @GetMapping("/info")
    public Object getRule(@RequestParam(value = "scriptId") String scriptId) {
        return scriptService.get(scriptId);
    }


    /**
     * 新增脚本
     *
     * @param script
     * @throws KvStorageException
     */
    @PostMapping("/add")
    public Object addRule(@RequestBody Script script) throws Exception {
        String now = DateUtil.now();
        script.setUpdateTime(now);
        scriptService.add(script);
        return script;
    }

    /**
     * 运行调试脚本
     *
     * @param script
     */
    @PostMapping("/run")
    public Object run(@RequestBody Script script) throws Exception {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
        scriptEngine.eval(script.getScriptContent());
        Invocable jsInvoke = (Invocable) scriptEngine;
        long time1 = System.currentTimeMillis();
        Object transform = jsInvoke.invokeFunction("transform", JacksonUtils.toObj(script.getParamContent(),Object.class));
        long time2 = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        result.put("result",new ObjectMapper().convertValue(transform, Map.class));
        result.put("time", time2 - time1);
        return result;
    }

    /**
     * 查询脚本
     *
     * @param script
     */
    @PostMapping("/list")
    public Object listRule(@RequestBody Script script) {
        return scriptService.list(script);
    }

    /**
     * 移除脚本
     *
     * @param script
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeRule(@RequestBody Script script) throws Exception {
        scriptService.remove(script);
    }

    /**
     * 更新脚本
     *
     * @param script
     * @throws Exception
     */
    @PutMapping("/update")
    public Object updateRule(@RequestBody Script script) throws Exception {
        String now = DateUtil.now();
        script.setUpdateTime(now);
        scriptService.update(script);
        return script;
    }

}

