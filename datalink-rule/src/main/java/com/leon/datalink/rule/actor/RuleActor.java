package com.leon.datalink.rule.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.actor.DriverActor;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.transform.TransformHandler;
import com.leon.datalink.rule.transform.TransformHandlerFactory;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.runtime.entity.RuntimeStatus;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private List<ActorRef> destActorRefList;

    private TransformHandler transformHandler;

    public RuleActor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public void preStart() {
        // 创建数据转换
        RuntimeStatus runtimeStatus = new RuntimeStatus(RuntimeTypeEnum.TRANSFORM);
        try {
            transformHandler = TransformHandlerFactory.getHandler(rule.getTransformMode().getTransformHandler());
            if (null != transformHandler) {
                transformHandler.init(rule);
            }
            runtimeStatus.normal();
        } catch (Exception e) {
            Loggers.RULE.error("create transform handler error {}", e.getMessage());
            runtimeStatus.abnormal(e.getMessage());
        } finally {
            RuntimeManger.handleStatus(rule.getRuleId(), runtimeStatus);
        }

        ActorContext context = getContext();

        // 创建目的actor
        destActorRefList = rule.getDestResourceList().stream().map(destResource -> context.actorOf((Props.create(DriverActor.class, destResource.getResourceType().getDriver(), destResource.getProperties(), DriverModeEnum.DEST, rule.getRuleId(), destResource.getResourceRuntimeId())),
                "dest-" + destResource.getResourceRuntimeId())).collect(Collectors.toList());

        // 创建源actor
        rule.getSourceResourceList().forEach(sourceResource -> context.actorOf((Props.create(DriverActor.class, sourceResource.getResourceType().getDriver(), sourceResource.getProperties(), DriverModeEnum.SOURCE, rule.getRuleId(), sourceResource.getResourceRuntimeId())),
                "source-" + sourceResource.getResourceRuntimeId()));

        Loggers.RULE.info("started rule [{}]", getSelf().path());
    }

    @Override
    public void postStop() {
        RuntimeStatus runtimeStatus = new RuntimeStatus(RuntimeTypeEnum.TRANSFORM);
        try {
            if (null != transformHandler) {
                transformHandler.destroy();
            }
            runtimeStatus.init();
            Loggers.RULE.info("stop  rule [{}]", getSelf().path());
        } catch (Exception e) {
            Loggers.RULE.info("stop  rule error {}", e.getMessage());
            runtimeStatus.abnormal(e.getMessage());
        } finally {
            RuntimeManger.handleStatus(rule.getRuleId(), runtimeStatus);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuntimeData.class, dataRecord -> {
            RuntimeTypeEnum type = dataRecord.getType();
            if (!RuntimeTypeEnum.SOURCE.equals(type)) return;

            RuntimeData transformRecord = new RuntimeData(RuntimeTypeEnum.TRANSFORM);

            try {
                Object transformData = transformHandler.transform(dataRecord.getData());

                // 忽略空值
                if (!(null == transformData && rule.isIgnoreNullValue())) {
                    // 发送给所有目的driver
                    for (ActorRef actorRef : destActorRefList) {
                        actorRef.tell(transformRecord, getSelf());
                    }
                }

                transformRecord.success(transformData);
            } catch (Exception e) {
                Loggers.DRIVER.error("transform data error: {}", e.getMessage());
                transformRecord.fail(e.getMessage());
            } finally {
                RuntimeManger.handleRecord(rule.getRuleId(), transformRecord);
            }


        }).build();
    }


}
