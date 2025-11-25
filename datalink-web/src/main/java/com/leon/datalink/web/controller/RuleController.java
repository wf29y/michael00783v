package com.leon.datalink.web.controller;

import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.web.rule.RuleService;
import com.leon.datalink.web.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
     * 查询规则
     *
     * @param ruleId
     */
    @GetMapping("/info")
    public Object getRule(@RequestParam(value = "ruleId") String ruleId) {
        return ruleService.get(ruleId);
    }


    /**
     * 新增规则
     *
     * @param rule
     * @throws Exception
     */
    @PostMapping("/add")
    public void addRule(@RequestBody Rule rule) throws Exception {
        ValidatorUtil.isNotEmpty(rule.getRuleName(), rule.getTransformMode(), rule.getSourceResourceList(), rule.getDestResourceList());

        for (Resource resource : rule.getSourceResourceList()) {
            if(StringUtils.isEmpty(resource.getResourceRuntimeId())){
                resource.setResourceRuntimeId(SnowflakeIdWorker.getId());
            }
        }
        for (Resource resource : rule.getDestResourceList()) {
            if(StringUtils.isEmpty(resource.getResourceRuntimeId())){
                resource.setResourceRuntimeId(SnowflakeIdWorker.getId());
            }
        }

        ruleService.add(rule);
    }

    /**
     * 查询规则
     *
     * @param rule
     */
    @PostMapping("/list")
    public Object listRule(@RequestBody Rule rule) {
        return ruleService.list(rule);
    }

    /**
     * 移除规则
     *
     * @param rule
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.remove(ruleId);
    }

    /**
     * 更新规则
     *
     * @param rule
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateRule(@RequestBody Rule rule) throws Exception {
        ValidatorUtil.isNotEmpty(rule.getRuleId(),rule.getRuleName(), rule.getTransformMode(), rule.getSourceResourceList(), rule.getDestResourceList());

        for (Resource resource : rule.getSourceResourceList()) {
            if(StringUtils.isEmpty(resource.getResourceRuntimeId())){
                resource.setResourceRuntimeId(SnowflakeIdWorker.getId());
            }
        }
        for (Resource resource : rule.getDestResourceList()) {
            if(StringUtils.isEmpty(resource.getResourceRuntimeId())){
                resource.setResourceRuntimeId(SnowflakeIdWorker.getId());
            }
        }

        ruleService.update(rule);
    }

    /**
     * 启动规则
     */
    @PostMapping("/start")
    public void startRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.startRule(ruleId);
    }

    /**
     * 停止规则
     */
    @PostMapping("/stop")
    public void stopRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.stopRule(ruleId);
    }

}

