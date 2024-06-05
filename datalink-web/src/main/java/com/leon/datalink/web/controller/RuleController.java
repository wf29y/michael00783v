package com.leon.datalink.web.controller;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.rule.Rule;
import com.leon.datalink.web.rule.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName RulesController
 * @Description 规则管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    /**
     * 新增规则
     *
     * @param ruleId
     * @throws KvStorageException
     */
    @GetMapping("/info")
    public Object getRule(@RequestParam(value = "ruleId") String ruleId)  {
       return ruleService.getRule(ruleId);
    }


    /**
     * 新增规则
     *
     * @param rule
     * @throws KvStorageException
     */
    @PostMapping("/add")
    public void addRule(@RequestBody Rule rule) throws KvStorageException {
        ruleService.addRule(rule);
    }

    /**
     * 查询规则
     *
     * @param rule
     */
    @PostMapping("/list")
    public Object listRule(@RequestBody Rule rule) {
        return ruleService.listRule(rule);
    }

    /**
     * 移除规则
     *
     * @param rule
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeRule(@RequestBody Rule rule) throws Exception {
        ruleService.removeRule(rule);
    }

    /**
     * 更新规则
     *
     * @param rule
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateRule(@RequestBody Rule rule) throws Exception {
        ruleService.updateRule(rule);
    }

    /**
     * 启动规则
     */
    @PostMapping("/start")
    public void startRule(@RequestBody Rule rule) throws Exception {
        ruleService.startRule(rule);
    }

    /**
     * 停止规则
     */
    @PostMapping("/stop")
    public void stopRule(@RequestBody Rule rule) throws Exception {
        ruleService.stopRule(rule);
    }

}

