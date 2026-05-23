package com.leon.datalink.web.controller;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.web.service.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName RuntimeController
 * @Description 运行状态管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/runtime")
public class RuntimeController {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 查询运行状态
     *
     * @param ruleId
     */
    @GetMapping("/info")
    public Object getRuntime(@RequestParam(value = "ruleId") String ruleId) {
        return runtimeService.getRuntime(ruleId);
    }


    /**
     * 重置运行状态
     *
     * @param ruleId
     */
    @GetMapping("/reset")
    public void resetRuntime(@RequestParam(value = "ruleId") String ruleId) throws KvStorageException {
        runtimeService.resetRuntime(ruleId);
    }


}

