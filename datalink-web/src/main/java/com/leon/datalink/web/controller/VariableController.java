package com.leon.datalink.web.controller;

import com.leon.datalink.core.variable.Variable;
import com.leon.datalink.web.variable.VariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName VariableController
 * @Description 全局变量
 * @Author Leon
 * @Date 2022年8月16日18:40:50
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/variable")
public class VariableController {

    @Autowired
    private VariableService variableService;

    /**
     * 新增资源
     *
     * @param variable
     * @throws Exception
     */
    @PostMapping("/add")
    public void addVariable(@RequestBody Variable variable) throws Exception {
        variableService.add(variable);
    }

    /**
     * 查询资源
     *
     * @param variable
     */
    @PostMapping("/list")
    public Object listVariable(@RequestBody Variable variable) {
        return variableService.list(variable);
    }

    /**
     * 移除资源
     *
     * @param variable
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeVariable(@RequestBody Variable variable) throws Exception {
        variableService.remove(variable);
    }

    /**
     * 更新资源
     *
     * @param variable
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateVariable(@RequestBody Variable variable) throws Exception {
        variableService.update(variable);
    }



}

