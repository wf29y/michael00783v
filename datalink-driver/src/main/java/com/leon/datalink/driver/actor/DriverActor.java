package com.leon.datalink.driver.actor;

import akka.actor.AbstractActor;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverFactory;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.entity.DriverProperties;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
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

    private final DriverProperties driverProperties;

    private final String ruleId;

    private final String resourceRuntimeId;


    public DriverActor(Class<? extends Driver> driverClass, DriverProperties driverProperties, DriverModeEnum driverMode, String ruleId, String resourceRuntimeId) throws Exception {
        this.driverMode = driverMode;
        this.driverProperties = driverProperties;
        this.ruleId = ruleId;
        this.resourceRuntimeId = resourceRuntimeId;
        this.driver = DriverFactory.getDriver(driverClass);
        this.driver.init(getSelf(), ruleId);
    }

    @Override
    public void preStart() {
        Loggers.DRIVER.info("start driver [{}]", getSelf().path());
        RuntimeTypeEnum typeEnum = DriverModeEnum.SOURCE.equals(driverMode) ? RuntimeTypeEnum.SOURCE : RuntimeTypeEnum.DEST;
        RuntimeStatus runtimeStatus = new RuntimeStatus(typeEnum);
        try {
            // 创建驱动
            driver.create(driverMode, driverProperties);
            runtimeStatus.normal();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("driver actor start error {} : {}", driver.getClass(), e.getMessage());
        } finally {
            // 结果发送到runtime
            RuntimeManger.handleStatus(ruleId, resourceRuntimeId, runtimeStatus);
        }
    }

    @Override
    public void postStop() {
        Loggers.DRIVER.info("stop  driver [{}]", getSelf().path());
        RuntimeTypeEnum typeEnum = DriverModeEnum.SOURCE.equals(driverMode) ? RuntimeTypeEnum.SOURCE : RuntimeTypeEnum.DEST;
        RuntimeStatus runtimeStatus = new RuntimeStatus(typeEnum);
        try {
            // 销毁驱动
            driver.destroy(driverMode, driverProperties);
            runtimeStatus.init();
        } catch (Exception e) {
            runtimeStatus.abnormal(e.getMessage());
            Loggers.DRIVER.error("driver actor stop error {} : {}", driver.getClass(), e.getMessage());
        } finally {
            // 结果发送到runtime
            RuntimeManger.handleStatus(ruleId, resourceRuntimeId, runtimeStatus);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RuntimeStatus.class, runtimeStatus -> RuntimeManger.handleStatus(ruleId, resourceRuntimeId, runtimeStatus))
                .match(RuntimeData.class, runtimeData -> {
                            switch (runtimeData.getType()) {
                                // 驱动产生数据
                                case SOURCE: {
                                    // 传给rule actor
                                    if (!runtimeData.isError()) getContext().getParent().tell(runtimeData, getSelf());
                                    // 结果发送到runtime
                                    RuntimeManger.handleRecord(ruleId, resourceRuntimeId, runtimeData);
                                    break;
                                }
                                // 驱动处理数据
                                case TRANSFORM: {
                                    RuntimeData destRecord = new RuntimeData(RuntimeTypeEnum.DEST);
                                    try {
                                        // 调用驱动处理数据
                                        Object publishResult = driver.handleData(runtimeData.getData(), driverProperties);
                                        destRecord.success(publishResult);
                                    } catch (Exception e) {
                                        Loggers.DRIVER.error("driver actor handle data error: {}", e.getMessage());
                                        destRecord.fail(e.getMessage());
                                    } finally {
                                        // 结果发送到runtime
                                        RuntimeManger.handleRecord(ruleId, resourceRuntimeId, destRecord);
                                    }
                                    break;
                                }
                            }
                        }
                ).build();
    }


}
