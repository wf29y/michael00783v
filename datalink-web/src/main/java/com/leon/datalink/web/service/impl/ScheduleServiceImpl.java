package com.leon.datalink.web.service.impl;

import com.leon.datalink.core.schedule.Schedule;
import com.leon.datalink.core.schedule.ScheduleManager;
import com.leon.datalink.web.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Override
    public List<Schedule> list(Schedule schedule) {
        Stream<Schedule> stream = ScheduleManager.getSchedules().stream();
        if (null != schedule) {
            if (null != schedule.getResourceName()) {
                stream = stream.filter(r -> r.getResourceName().contains(schedule.getResourceName()));
            }
        }
        return stream.collect(Collectors.toList());
    }

}
