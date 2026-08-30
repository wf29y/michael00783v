package com.leon.datalink.core.schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleManager {

    private static final List<Schedule> schedules = new ArrayList<>();

    public static void add(Schedule schedule) {
        schedules.add(schedule);
    }

    public static void stopByRuleId(String ruleId) {
        schedules.stream().filter(schedule -> schedule.getRuleId().equals(ruleId)).forEach(schedule -> schedule.getCancellable().cancel());
        schedules.removeIf(schedule -> schedule.getRuleId().equals(ruleId));
    }

    public static List<Schedule> getSchedules(){
        return schedules;
    }

}
