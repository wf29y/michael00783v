package com.leon.datalink.core.schedule;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension;
import scala.Option;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ScheduleManager {

    private static final List<Schedule> schedules = new ArrayList<>();
    private static QuartzSchedulerExtension quartzSchedule;

    public static void init(ActorSystem actorSystem) {
        quartzSchedule = new QuartzSchedulerExtension(actorSystem);
    }

    public static void create(ActorRef actorRef, Schedule schedule) {
        quartzSchedule.createSchedule(schedule.getScheduleName(), Option.empty(), schedule.getCronExpression(), Option.empty(), TimeZone.getDefault());
        ZonedDateTime jobStartTime = Instant.now().atZone(ZoneId.systemDefault()).plus(Duration.of(schedule.getInitialDelay(), ChronoUnit.valueOf(schedule.getInitialDelayUnit())));
        quartzSchedule.schedule(schedule.getScheduleName(), actorRef, new ScheduleTrigger(), Option.apply(Date.from(jobStartTime.toInstant())));
        schedules.add(schedule);
    }

    public static void stopByRuleId(String ruleId) {
        schedules.stream().filter(schedule -> schedule.getRuleId().equals(ruleId)).forEach(schedule -> quartzSchedule.deleteJobSchedule(schedule.getScheduleName()));
        schedules.removeIf(schedule -> schedule.getRuleId().equals(ruleId));
    }

    public static List<Schedule> getSchedules() {
        return schedules;
    }

    public static void onSystemDestroy() {
        schedules.clear();
        quartzSchedule.deleteAll();
        quartzSchedule.shutdown(false);
    }

}
