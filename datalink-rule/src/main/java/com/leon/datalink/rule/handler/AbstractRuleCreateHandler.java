package com.leon.datalink.rule.handler;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import cn.hutool.core.date.DateUtil;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.schedule.Schedule;
import com.leon.datalink.core.schedule.ScheduleManager;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.resource.entity.Resource;
import com.leon.datalink.rule.entity.Rule;
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

    protected void createSchedule(Resource resource, ActorRef actorRef) {
        ConfigProperties properties = resource.getProperties();
        Schedule schedule = new Schedule();
        schedule.setScheduleName(SnowflakeIdWorker.getId());
        schedule.setRuleId(rule.getRuleId());
        schedule.setResourceName(resource.getResourceName());
        schedule.setInitialDelay(properties.getLong(INITIAL_DELAY));
        schedule.setInitialDelayUnit(properties.getString(INITIAL_DELAY_UNIT));
        schedule.setCronExpression(properties.getString(CRON_EXPRESSION));
        schedule.setCreateTime(DateUtil.now());
        ScheduleManager.create(actorRef, schedule);
    }

}
