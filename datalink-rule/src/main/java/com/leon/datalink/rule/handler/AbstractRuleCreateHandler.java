package com.leon.datalink.rule.handler;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.schedule.Schedule;
import com.leon.datalink.core.schedule.ScheduleManager;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.resource.entity.Resource;
import com.leon.datalink.resource.entity.ScheduleTrigger;
import com.leon.datalink.rule.entity.Rule;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

import static com.leon.datalink.core.common.Constants.*;

public abstract class AbstractRuleCreateHandler implements RuleCreateHandler {

    protected Rule rule;

    protected ActorContext context;

    protected ActorRef ruleActorRef;

    @Override
    public void create(Rule rule, ActorContext context) {
        this.rule = rule;
        this.context = context;
        this.ruleActorRef = context.self();

        // 创建目的资源
        ActorRef destResourceActorRef = createDestResource();

        // 创建转换
        LinkedList<ActorRef> transformActorRefList = createTransform(destResourceActorRef);

        // 创建数据源
        createSourceResource(transformActorRefList);

    }

    protected abstract ActorRef createDestResource();

    protected abstract LinkedList<ActorRef> createTransform(ActorRef destResourceActorRef);

    protected abstract void createSourceResource(LinkedList<ActorRef> transformActorRefList);

    protected void createSchedule(Resource resource, ActorRef actorRef){
        ConfigProperties properties = resource.getProperties();

        Long initialDelay = properties.getLong(INITIAL_DELAY);
        String initialDelayUnit = properties.getString(INITIAL_DELAY_UNIT);

        Long interval = properties.getLong(INTERVAL);
        String intervalUnit = properties.getString(INTERVAL_UNIT);

        Cancellable cancellable = context.system().scheduler().scheduleAtFixedRate(
                Duration.of(initialDelay, ChronoUnit.valueOf(initialDelayUnit)),
                Duration.of(interval, ChronoUnit.valueOf(intervalUnit)),
                actorRef,
                new ScheduleTrigger(),
                context.dispatcher(),
                ActorRef.noSender());

        Schedule schedule = new Schedule();
        schedule.setId(SnowflakeIdWorker.getId());
        schedule.setRuleId(rule.getRuleId());
        schedule.setResourceName(resource.getResourceName());
        schedule.setInitialDelay(initialDelay);
        schedule.setInitialDelayUnit(initialDelayUnit);
        schedule.setInterval(interval);
        schedule.setIntervalUnit(intervalUnit);
        schedule.setCancellable(cancellable);
        schedule.setCreateTime(DateUtil.now());
        ScheduleManager.add(schedule);
    }

}
