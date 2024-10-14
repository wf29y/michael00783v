package com.leon.datalink.rule.actor;

import akka.actor.*;
import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.actor.*;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.runtime.RuntimeService;
import com.leon.datalink.rule.runtime.actor.RuntimeUpdateMsg;
import com.leon.datalink.rule.runtime.actor.RuntimeActor;
import org.springframework.util.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private final RuntimeService runtimeService;

    private final ActorSystem actorSystem = getContext().getSystem();

    private ActorRef runtimeActorRef;

    private ActorRef sourceActorRef;

    private List<ActorRef> destActorRefList;

    public RuleActor(Rule rule, RuntimeService runtimeService) {
        this.rule = rule;
        this.runtimeService = runtimeService;
    }

    @Override
    public void preStart() {
        Loggers.RULE.info("rule [{}] actor start", rule.getRuleId());
        String ruleId = rule.getRuleId();
        // 创建runtime actor
        runtimeActorRef = actorSystem.actorOf((Props.create(RuntimeActor.class, rule, runtimeService)), "runtime-" + rule.getRuleId());
        // 创建目的actor
        destActorRefList = rule.getDestResourceList().stream().map(destResource -> actorSystem.actorOf((Props.create(DriverActor.class, destResource.getResourceType().getDriver(), destResource.getProperties(), DriverModeEnum.DEST, this.getSelf())),
                String.format("rule-%s-dest-%s", ruleId, SnowflakeIdWorker.getId()))).collect(Collectors.toList());
        // 创建源actor
        Resource sourceResource = rule.getSourceResource();
        sourceActorRef = actorSystem.actorOf((Props.create(DriverActor.class, sourceResource.getResourceType().getDriver(), sourceResource.getProperties(), DriverModeEnum.SOURCE, this.getSelf())), String.format("rule-%s-source-%s", ruleId, SnowflakeIdWorker.getId()));
    }

    @Override
    public void postStop() {
        Loggers.RULE.info("rule [{}] actor stop", rule.getRuleId());
        // 停止源driver
        actorSystem.stop(sourceActorRef);
        // 停止目的driver
        destActorRefList.forEach(actorSystem::stop);
        // 停止runtime
        actorSystem.stop(runtimeActorRef);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DriverDataMsg.class, this::transform).build();
    }

    private void transform(DriverDataMsg msg) {
        Map data = msg.getData();

        Map result = null;
        boolean success = true;
        try {
            switch (rule.getAnalysisMode()) {
                case WITHOUT: {
                    result = data;
                    break;
                }
                case SCRIPT: {
                    result = scriptHandler(rule.getScript(), data);
                    break;
                }
                case JAR: {
                    // todo jar包解析
                    break;
                }
            }
        } catch (Exception e) {
            success = false;
        } finally {
            //发送到runtime
            RuntimeUpdateMsg runtimeUpdateMsg = new RuntimeUpdateMsg(data, success, new DateTime());
            runtimeActorRef.tell(runtimeUpdateMsg, ActorRef.noSender());
        }
        // 忽略空值
        if (null == result && rule.isIgnoreNullValue()) return;

        // 发送给所有目的driver
        for (ActorRef actorRef : destActorRefList) {
            actorRef.tell(new DriverDataMsg(result), ActorRef.noSender());
        }
    }


    // 脚本解析
    private Map scriptHandler(String script, Object data) {
        if (StringUtils.isEmpty(script) || null == data) {
            return null;
        }
        try {
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
            scriptEngine.eval(script);
            Invocable jsInvoke = (Invocable) scriptEngine;
            Object transform = jsInvoke.invokeFunction("transform", data);
            return new ObjectMapper().convertValue(transform, Map.class);
        } catch (Exception e) {
            Loggers.RULE.error("script error {}", e.getMessage());
        }
        return null;
    }


}
