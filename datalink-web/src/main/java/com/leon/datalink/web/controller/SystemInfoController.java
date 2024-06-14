package com.leon.datalink.web.controller;

import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.model.SystemInfo;
import com.leon.datalink.web.resource.ResourceService;
import com.leon.datalink.web.rule.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassNameSystemInfoController
 * @Description
 * @Author Leon
 * @Date 2022/4/11 10:34
 * @Version V1.0
 **/
@RestController
@RequestMapping({"/api/system"})
public class SystemInfoController {

    @Autowired
    private ResourceService northResourceService;

    @Autowired
    private RuleService ruleService;

    @GetMapping("/info")
    public Object getSystemInfo() {
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setResourceCount(northResourceService.getCount(new Resource()));
        systemInfo.setRuleCount(ruleService.getCount(new Rule()));
        return systemInfo;
    }
}
