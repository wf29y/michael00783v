package com.leon.datalink.web.controller;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.web.resource.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 新增资源
     * @param resource
     * @throws KvStorageException
     */
    @PostMapping("/add")
    public void addResource(@RequestBody Resource resource) throws KvStorageException {
        resourceService.addResource(resource);
    }

    /**
     * 查询资源
     * @param resource
     */
    @PostMapping("/list")
    public Object listResource(@RequestBody Resource resource) {
       return resourceService.listResource(resource);
    }

    /**
     *  移除资源
     * @param resource
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeResource(@RequestBody Resource resource) throws Exception {
        resourceService.removeResource(resource);
    }

    /**
     * 更新资源
     * @param resource
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateResource(@RequestBody Resource resource) throws Exception {
        resourceService.updateResource(resource);
    }



}

