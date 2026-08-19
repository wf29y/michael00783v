package com.leon.datalink.rule.handler;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.entity.ScheduleTrigger;
import com.leon.datalink.rule.entity.Rule;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.leon.datalink.core.common.Constants.*;

public abstract class AbstractRuleCreateHandler implements RuleCreateHandler {

    protected Rule rule;

    protected ActorContext context;

    protected ActorRef ruleActorRef;

    protected List<Cancellable> schedules = new ArrayList<>();

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

    @Override
    public void destroy() {
        for (Cancellable schedule : schedules) {
            schedule.cancel();
        }
    }

    protected abstract ActorRef createDestResource();

    protected abstract LinkedList<ActorRef> createTransform(ActorRef destResourceActorRef);

    protected abstract void createSourceResource(LinkedList<ActorRef> transformActorRefList);

    protected void createSchedule(ConfigProperties properties,ActorRef actorRef){
        ChronoUnit timeUnit = ChronoUnit.valueOf(properties.getString(TIME_UNIT));
        Cancellable cancellable = context.system().scheduler().scheduleAtFixedRate(
                Duration.of(properties.getLong(INITIAL_DELAY), timeUnit),
                Duration.of(properties.getLong(PERIOD), timeUnit),
                actorRef,
                new ScheduleTrigger(),
                context.dispatcher(),
                ActorRef.noSender());

        schedules.add(cancellable);
    }

}
