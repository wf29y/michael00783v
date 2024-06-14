package com.leon.datalink.web.controller;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.rule.script.Script;
import com.leon.datalink.web.script.ScriptService;
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
    public Object getRule(@RequestParam(value = "scriptId") String scriptId)  {
       return scriptService.get(scriptId);
    }


    /**
     * 新增脚本
     *
     * @param script
     * @throws KvStorageException
     */
    @PostMapping("/add")
    public void addRule(@RequestBody Script script) throws KvStorageException {
        scriptService.add(script);
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
    public void updateRule(@RequestBody Script script) throws Exception {
        scriptService.update(script);
    }

}

