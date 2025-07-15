package com.leon.datalink.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.resource.ResourceService;
import com.leon.datalink.web.rule.RuleService;
import com.leon.datalink.web.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.List;

/**
 * @ClassName ResourcesController
 * @Description 资源管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/resource")
public class ResourcesController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RuleService ruleService;

    /**
     * 查询资源
     *
     * @param resourceId
     */
    @GetMapping("/info")
    public Object getResource(@RequestParam(value = "resourceId") String resourceId) {
        return resourceService.get(resourceId);
    }


    /**
     * 新增资源
     *
     * @param resource
     * @throws Exception
     */
    @PostMapping("/add")
    public void addResource(@RequestBody Resource resource) throws Exception {
        ValidatorUtil.isNotEmpty(resource.getResourceName(), resource.getResourceType(), resource.getProperties());
        resourceService.add(resource);
    }

    /**
     * 查询资源
     *
     * @param resource
     */
    @PostMapping("/list")
    public Object listResource(@RequestBody Resource resource) {
        return resourceService.list(resource);
    }

    /**
     * 移除资源
     *
     * @param resource
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeResource(@RequestBody Resource resource) throws Exception {
        String resourceId = resource.getResourceId();
        ValidatorUtil.isNotEmpty(resourceId);

        List<Rule> list = ruleService.list(new Rule().setSearchResourceId(resourceId));
        if(CollectionUtil.isNotEmpty(list)){
            throw new ValidationException("该资源已被规则绑定,无法删除");
        }

        resourceService.remove(resourceId);
    }

    /**
     * 更新资源
     *
     * @param resource
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateResource(@RequestBody Resource resource) throws Exception {
        ValidatorUtil.isNotEmpty(resource.getResourceId(), resource.getResourceName(), resource.getResourceType(), resource.getProperties());
        resourceService.update(resource);
    }

    /**
     * 测试资源
     *
     * @param resource
     * @throws Exception
     */
    @PostMapping("/test")
    public boolean testResource(@RequestBody Resource resource) throws Exception {
        ValidatorUtil.isNotEmpty(resource.getResourceName(), resource.getResourceType(), resource.getProperties());
        return resourceService.testDriver(resource);
    }

}

