package com.leon.datalink.web.controller;

import com.leon.datalink.core.schedule.Schedule;
import com.leon.datalink.web.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ScheduleController
 * @Description 定时调度
 * @Author Leon
 * @Date 2023年1月12日13:09:56
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 查询
     */
    @PostMapping("/list")
    public Object list(@RequestBody Schedule schedule) {
        return scheduleService.list(schedule);
    }

}

