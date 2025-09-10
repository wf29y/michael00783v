package com.leon.datalink.rule.actor;

import akka.actor.*;
import cn.hutool.core.date.DateTime;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.actor.*;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.transform.TransformHandler;
import com.leon.datalink.rule.transform.TransformHandlerFactory;
import com.leon.datalink.runtime.actor.RuntimeActor;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private ActorRef runtimeActorRef;

    private List<ActorRef> destActorRefList;

    private TransformHandler transformHandler;

    public RuleActor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public void preStart() {
        // 创建数据转换
        try {
            transformHandler = TransformHandlerFactory.getHandler(rule.getTransformMode().getTransformHandler());
            if (null != transformHandler) {
                transformHandler.init(rule);
            }
        } catch (Exception e) {
            Loggers.RULE.error("create transform handler error {}", e.getMessage());
        }

        ActorContext context = getContext();

        // 创建runtime actor
        runtimeActorRef = context.actorOf((Props.create(RuntimeActor.class, rule.getRuleId(), new HashMap<>(rule.getVariables()))), "runtime");

        // 创建目的actor
        destActorRefList = rule.getDestResourceList().stream().map(destResource -> context.actorOf((Props.create(DriverActor.class, destResource.getResourceType().getDriver(), destResource.getProperties(), DriverModeEnum.DEST, rule.getRuleId())),
                "dest-" + SnowflakeIdWorker.getId())).collect(Collectors.toList());

        // 创建源actor
        rule.getSourceResourceList().forEach(destResource -> context.actorOf((Props.create(DriverActor.class, destResource.getResourceType().getDriver(), destResource.getProperties(), DriverModeEnum.SOURCE, rule.getRuleId())),
                "source-" + SnowflakeIdWorker.getId()));

        Loggers.RULE.info("started rule [{}]", getSelf().path());
    }

    @Override
    public void postStop() {
        if (null != transformHandler) {
            transformHandler.destroy();
        }
        Loggers.RULE.info("stop  rule [{}]", getSelf().path());    // 子actor自动停止
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ReceiveDataMsg.class, (msg) -> {
            Object data = msg.getData();

            RuntimeUpdateDataMsg runtimeMsg = new RuntimeUpdateDataMsg();
            runtimeMsg.setReceiveData(data);
            runtimeMsg.setTime(DateTime.now());

            Object transformData = null;
            try {
                transformData = transformHandler.transform(data);
                runtimeMsg.setTransformSuccess(true);
                runtimeMsg.setTransformData(transformData);
            } catch (Exception e) {
                Loggers.DRIVER.error("transform data error: {}", e.getMessage());
                runtimeMsg.setTransformSuccess(false);
                runtimeMsg.setMessage(e.getMessage());
                runtimeActorRef.tell(runtimeMsg, getSelf());
            }

            // 忽略空值
            if (null == transformData && rule.isIgnoreNullValue()) {
                runtimeMsg.setMessage("ignore null value");
                runtimeActorRef.tell(runtimeMsg, getSelf());
                return;
            }

            // 发送给所有目的driver
            for (ActorRef actorRef : destActorRefList) {
                actorRef.tell(new PublishDataMsg(transformData, runtimeActorRef, runtimeMsg), getSelf());
            }
        }).build();
    }


}
