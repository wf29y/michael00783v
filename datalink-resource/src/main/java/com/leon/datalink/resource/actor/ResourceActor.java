package com.leon.datalink.resource.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.Driver;
import com.leon.datalink.resource.DriverFactory;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.runtime.entity.RuntimeStatus;

/**
 * @ClassName ResourceActor
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:05
 * @Version V1.0
 **/
public class ResourceActor extends AbstractActor {

    private final Driver driver;

    private final DriverModeEnum driverMode;

    private final Resource resource;

    private final ActorRef ruleActorRef;

    public static Props props(Resource resource, DriverModeEnum driverMode, ActorRef ruleActorRef) {
        return Props.create(ResourceActor.class, () -> new ResourceActor(resource, driverMode, ruleActorRef, null));
    }

    public static Props props(Resource resource, DriverModeEnum driverMode, ActorRef ruleActorRef, ActorRef transformActorRef) {
        return Props.create(ResourceActor.class, () -> new ResourceActor(resource, driverMode, ruleActorRef, transformActorRef));
    }

    public ResourceActor(Resource resource, DriverModeEnum driverMode, ActorRef ruleActorRef, ActorRef transformActorRef) throws Exception {
        this.resource = resource;
        this.driverMode = driverMode;
        this.ruleActorRef = ruleActorRef;
        this.driver = DriverFactory.getDriver(resource.getResourceType().getDriver());
        this.driver.init(runtimeData -> {
            if (!runtimeData.isError()) {
                runtimeData.setEntityRuntimeId(resource.getResourceRuntimeId());
                transformActorRef.tell(runtimeData, getSelf());
            }
            ruleActorRef.tell(runtimeData, getSelf());
        });
    }

    @Override
    public void preStart() {
        Loggers.DRIVER.info("start resource [{}]", getSelf().path());

        RuntimeStatus runtimeStatus = new RuntimeStatus(DriverModeEnum.SOURCE.equals(driverMode) ? RuntimeTypeEnum.SOURCE : RuntimeTypeEnum.DEST, resource.getResourceRuntimeId());
        try {
            // 创建驱动
            driver.create(driverMode, resource.getProperties());
            runtimeStatus.normal();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("resource actor start error {} : {}", driver.getClass(), e.getMessage());
        } finally {
            // 结果发送到runtime
            ruleActorRef.tell(runtimeStatus, getSelf());
        }
    }

    @Override
    public void postStop() {
        Loggers.DRIVER.info("stop resource [{}]", getSelf().path());
        try {
            // 销毁驱动
            driver.destroy(driverMode, resource.getProperties());
        } catch (Exception e) {
            Loggers.DRIVER.error("resource actor stop error {} : {}", driver.getClass(), e.getMessage());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuntimeData.class, this::handleData).build();
    }

    // 作为目的资源时,处理来自transform的数据
    private void handleData(RuntimeData runtimeData) {
        if (runtimeData.getType() == RuntimeTypeEnum.TRANSFORM) {
            RuntimeData destRecord = new RuntimeData(RuntimeTypeEnum.DEST, resource.getResourceRuntimeId());
            try {
                Object publishResult = driver.handleData(runtimeData.getData(), resource.getProperties());
                destRecord.success(publishResult);
            } catch (Exception e) {
                Loggers.DRIVER.error("resource actor handle data error: {}", e.getMessage());
                destRecord.fail(e.getMessage());
            } finally {
                ruleActorRef.tell(destRecord, getSelf());
            }
        }
    }


}
