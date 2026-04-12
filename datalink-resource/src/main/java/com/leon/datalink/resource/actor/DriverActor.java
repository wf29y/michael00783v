package com.leon.datalink.resource.actor;

import akka.actor.AbstractActor;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.Driver;
import com.leon.datalink.resource.DriverFactory;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.runtime.entity.RuntimeStatus;

/**
 * @ClassName DriverActor
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:05
 * @Version V1.0
 **/
public class DriverActor extends AbstractActor {

    private final Driver driver;

    private final DriverModeEnum driverMode;

    private final Resource resource;

    public DriverActor(Resource resource, DriverModeEnum driverMode) throws Exception {
        this.resource = resource;
        this.driverMode = driverMode;
        this.driver = DriverFactory.getDriver(resource.getResourceType().getDriver());
        this.driver.init(getSelf(), resource.getRuleId());
    }

    @Override
    public void preStart() {
        Loggers.DRIVER.info("start driver [{}]", getSelf().path());

        RuntimeStatus runtimeStatus = new RuntimeStatus(DriverModeEnum.SOURCE.equals(driverMode) ? RuntimeTypeEnum.SOURCE : RuntimeTypeEnum.DEST, resource.getResourceRuntimeId());
        try {
            // 创建驱动
            driver.create(driverMode, resource.getProperties());
            runtimeStatus.normal();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("driver actor start error {} : {}", driver.getClass(), e.getMessage());
        } finally {
            // 结果发送到runtime
            RuntimeManger.handleStatus(resource.getRuleId(), runtimeStatus);
        }
    }

    @Override
    public void postStop() {
        Loggers.DRIVER.info("stop driver [{}]", getSelf().path());
        RuntimeStatus runtimeStatus = new RuntimeStatus(DriverModeEnum.SOURCE.equals(driverMode) ? RuntimeTypeEnum.SOURCE : RuntimeTypeEnum.DEST, resource.getResourceRuntimeId());
        try {
            // 销毁驱动
            driver.destroy(driverMode, resource.getProperties());
            runtimeStatus.init();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("driver actor stop error {} : {}", driver.getClass(), e.getMessage());
        } finally {
            // 结果发送到runtime
            RuntimeManger.handleStatus(resource.getRuleId(), runtimeStatus);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuntimeData.class, runtimeData -> {
                    switch (runtimeData.getType()) {
                        // 驱动产生数据
                        case SOURCE: {
                            // 传给rule actor
                            if (!runtimeData.isError()) {
                                runtimeData.setEntityRuntimeId(resource.getResourceRuntimeId());
                                getContext().getParent().tell(runtimeData, getSelf());
                            }
                            // 结果发送到runtime
                            RuntimeManger.handleRecord(resource.getRuleId(), runtimeData);
                            break;
                        }
                        // 驱动处理数据
                        case TRANSFORM: {
                            RuntimeData destRecord = new RuntimeData(RuntimeTypeEnum.DEST, resource.getResourceRuntimeId());
                            try {
                                // 调用驱动处理数据
                                Object publishResult = driver.handleData(runtimeData.getData(), resource.getProperties());
                                destRecord.success(publishResult);
                            } catch (Exception e) {
                                Loggers.DRIVER.error("driver actor handle data error: {}", e.getMessage());
                                destRecord.fail(e.getMessage());
                            } finally {
                                // 结果发送到runtime
                                RuntimeManger.handleRecord(resource.getRuleId(), destRecord);
                            }
                            break;
                        }
                    }
                }
        )
                .build();
    }


}
