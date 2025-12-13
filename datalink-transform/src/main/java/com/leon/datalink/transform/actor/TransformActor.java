package com.leon.datalink.transform.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.runtime.entity.RuntimeStatus;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import com.leon.datalink.transform.handler.TransformHandlerFactory;

public class TransformActor extends AbstractActor {

    private final Transform transform;

    private final TransformHandler handler;

    private final ActorRef next;

    private final String ruleId;

    private final String transformRuntimeId;

    public TransformActor(Transform transform, ActorRef next, String ruleId, String transformRuntimeId) throws Exception {
        this.transform = transform;
        this.next = next;
        this.ruleId = ruleId;
        this.transformRuntimeId = transformRuntimeId;
        this.handler = TransformHandlerFactory.getHandler(transform.getTransformMode().getTransformHandler());
    }

    @Override
    public void preStart() throws Exception {
        Loggers.DRIVER.info("start transform [{}]", getSelf().path());
        RuntimeStatus runtimeStatus = new RuntimeStatus(RuntimeTypeEnum.TRANSFORM);
        try {
            transform.setRuleId(ruleId);
            handler.init(transform);
            runtimeStatus.normal();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("transform actor start error {} : {}", handler.getClass(), e.getMessage());
        } finally {
            RuntimeManger.handleStatus(ruleId, transformRuntimeId, runtimeStatus);
        }
    }

    @Override
    public void postStop() throws Exception {
        Loggers.DRIVER.info("stop transform [{}]", getSelf().path());
        RuntimeStatus runtimeStatus = new RuntimeStatus(RuntimeTypeEnum.TRANSFORM);
        try {
            handler.destroy();
            runtimeStatus.init();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("transform actor stop error {} : {}", handler.getClass(), e.getMessage());
        } finally {
            RuntimeManger.handleStatus(ruleId, transformRuntimeId, runtimeStatus);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuntimeData.class, dataRecord -> {
            RuntimeData transformRecord = new RuntimeData(RuntimeTypeEnum.TRANSFORM);
            try {
                Object transformData = handler.transform(dataRecord.getData());
                transformRecord.success(transformData);
                next.tell(transformRecord, getSelf());
            } catch (Exception e) {
                Loggers.DRIVER.error("transform data error: {}", e.getMessage());
                transformRecord.fail(e.getMessage());
            } finally {
                RuntimeManger.handleRecord(ruleId, transformRuntimeId, transformRecord);
            }
        }).build();
    }
}
