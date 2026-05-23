package com.leon.datalink.transform.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.runtime.entity.RuntimeStatus;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import com.leon.datalink.transform.handler.TransformHandlerFactory;

public class TransformActor extends AbstractActor {

    private final Transform transform;

    private final TransformHandler handler;

    private final ActorRef nextActorRef;

    private final ActorRef ruleActorRef;

    public TransformActor(Transform transform, ActorRef ruleActorRef, ActorRef nextActorRef) throws Exception {
        this.transform = transform;
        this.nextActorRef = nextActorRef;
        this.ruleActorRef = ruleActorRef;
        this.handler = TransformHandlerFactory.getHandler(transform.getTransformMode().getTransformHandler());
    }

    @Override
    public void preStart() throws Exception {
        Loggers.DRIVER.info("start transform [{}]", getSelf().path());
        RuntimeStatus runtimeStatus = new RuntimeStatus(RuntimeTypeEnum.TRANSFORM, transform.getTransformRuntimeId());
        try {
            handler.init(transform);
            runtimeStatus.normal();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("transform actor start error {} : {}", handler.getClass(), e.getMessage());
        } finally {
            ruleActorRef.tell(runtimeStatus,getSelf());
        }
    }

    @Override
    public void postStop() throws Exception {
        Loggers.DRIVER.info("stop transform [{}]", getSelf().path());
        RuntimeStatus runtimeStatus = new RuntimeStatus(RuntimeTypeEnum.TRANSFORM, transform.getTransformRuntimeId());
        try {
            handler.destroy();
            runtimeStatus.init();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("transform actor stop error {} : {}", handler.getClass(), e.getMessage());
        } finally {
            ruleActorRef.tell(runtimeStatus,getSelf());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuntimeData.class, dataRecord -> {
            RuntimeData transformRecord = new RuntimeData(RuntimeTypeEnum.TRANSFORM, transform.getTransformRuntimeId());
            try {
                handler.transform(dataRecord, transformRecord::success);
                nextActorRef.tell(transformRecord, getSelf());
            } catch (Exception e) {
                Loggers.DRIVER.error("transform data error: {}", e.getMessage());
                transformRecord.fail(e.getMessage());
            } finally {
                ruleActorRef.tell(transformRecord,getSelf());
            }
        }).build();
    }
}
