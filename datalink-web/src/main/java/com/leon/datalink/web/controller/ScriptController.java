package com.leon.datalink.web.controller;

import cn.hutool.core.date.DateUtil;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.transform.script.Script;
import com.leon.datalink.web.service.ScriptService;
import com.leon.datalink.web.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Object getScript(@RequestParam(value = "scriptId") String scriptId) {
        return scriptService.get(scriptId);
    }


    /**
     * 新增脚本
     *
     * @param script
     * @throws KvStorageException
     */
    @PostMapping("/add")
    public Object addScript(@RequestBody Script script) throws Exception {
        ValidatorUtil.isNotEmpty(script.getScriptName(), script.getScriptContent());
        script.setUpdateTime(DateUtil.now());
        scriptService.add(script);
        return script;
    }

    /**
     * 查询脚本
     *
     * @param script
     */
    @PostMapping("/list")
    public Object listScript(@RequestBody Script script) {
        return scriptService.list(script);
    }

    /**
     * 移除脚本
     *
     * @param script
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeScript(@RequestBody Script script) throws Exception {
        ValidatorUtil.isNotEmpty(script.getScriptId());
        scriptService.remove(script.getScriptId());
    }

    /**
     * 更新脚本
     *
     * @param script
     * @throws Exception
     */
    @PutMapping("/update")
    public Object updateScript(@RequestBody Script script) throws Exception {
        ValidatorUtil.isNotEmpty(script.getScriptId(), script.getScriptName(), script.getScriptContent());
        script.setUpdateTime(DateUtil.now());
        scriptService.update(script);
        return script;
    }

}

