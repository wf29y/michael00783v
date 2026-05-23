package com.leon.datalink.web.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.leon.datalink.core.utils.VersionUtils;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.model.DiskInfo;
import com.leon.datalink.web.model.SystemInfo;
import com.leon.datalink.web.model.SystemStatistics;
import com.leon.datalink.web.service.ResourceService;
import com.leon.datalink.web.service.RuleService;
import com.leon.datalink.web.util.BaseContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.leon.datalink.core.common.Constants.LOCAL_IP_PROPERTY_KEY;

/**
 * @ClassName SystemInfoController
 * @Description
 * @Author Leon
 * @Date 2022/4/11 10:34
 * @Version V1.0
 **/
@RestController
@RequestMapping({"/api/system"})
public class SystemInfoController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RuleService ruleService;

    @GetMapping("/info")
    public Object getSystemInfo() {
        SystemInfo info = new SystemInfo();
        info.setIp(System.getProperty(LOCAL_IP_PROPERTY_KEY));
        info.setVersion(VersionUtils.version);
        info.setTime(DateUtil.format(DateTime.now(), "yyyy-MM-dd HH:mm"));
        info.setUsername((String) BaseContextUtil.get(BaseContextUtil.USER_NAME));
        return info;
    }

    @GetMapping("/statistics")
    public Object getSystemStatistics() {
        SystemStatistics statistics = new SystemStatistics();
        statistics.setResourceCount(resourceService.getCount(new Resource()));
        statistics.setRuleCount(ruleService.getCount(new Rule()));
        return statistics;
    }

    @GetMapping("/disk")
    public Object getDiskInfo() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File[] fs = File.listRoots();
        return Arrays.stream(fs).map(disk -> {
            DiskInfo diskInfo = new DiskInfo();
            diskInfo.setName(fsv.getSystemDisplayName(disk));
            diskInfo.setTotal(disk.getTotalSpace());
            diskInfo.setFree(disk.getFreeSpace());
            return diskInfo;
        }).collect(Collectors.toList());
    }


}
