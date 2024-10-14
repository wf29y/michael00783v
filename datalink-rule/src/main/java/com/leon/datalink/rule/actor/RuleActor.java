package com.leon.datalink.rule.actor;

import akka.actor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.driver.DriverModeEnum;
import com.leon.datalink.driver.actor.*;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.constants.RuleAnalysisModeEnum;
import com.leon.datalink.rule.entity.Rule;
import org.springframework.util.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private final ActorSystem actorSystem = getContext().getSystem();

    public RuleActor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuleStartMsg.class, this::start)
                .match(RuleStopMsg.class, this::stop)
                .match(RuleTransformMsg.class, this::transform).build();
    }

    private void start(RuleStartMsg msg) {
        // 创建目的资源
        String destActorName = "rule-" + rule.getRuleId() + "-dest-";
        for (Resource destResource : rule.getDestResourceList()) {
            ActorRef actorRef = actorSystem.actorOf((Props.create(DriverActor.class)), destActorName + SnowflakeIdWorker.getId());
            DriverCreateMsg driverCreateMsg = new DriverCreateMsg(destResource.getResourceType().getDriver(), DriverModeEnum.DEST, destResource.getProperties(), this.getSelf());
            actorRef.tell(driverCreateMsg, ActorRef.noSender());
        }

        // 源资源
        String sourceActorName = "rule-" + rule.getRuleId() + "-source-";
        Resource sourceResource = rule.getSourceResource();
        ActorRef actorRef = actorSystem.actorOf((Props.create(DriverActor.class)), sourceActorName + SnowflakeIdWorker.getId());
        DriverCreateMsg driverCreateMsg = new DriverCreateMsg(sourceResource.getResourceType().getDriver(), DriverModeEnum.SOURCE, sourceResource.getProperties(), this.getSelf());
        actorRef.tell(driverCreateMsg, ActorRef.noSender());
    }

    private void stop(RuleStopMsg msg) {
        ActorSelection actorSelection = actorSystem.actorSelection("/user/rule-" + rule.getRuleId() + "-*");
        actorSelection.tell(new DriverCloseMsg(), ActorRef.noSender());
        actorSystem.stop(getSelf());
    }


    private void transform(RuleTransformMsg msg) {
        Map data = msg.getData();

        Map result = null;
        RuleAnalysisModeEnum analysisMode = rule.getAnalysisMode();
        switch (analysisMode) {
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
        // 忽略空值
        if (null == result && rule.isIgnoreNullValue()) return;

        ActorSelection actorSelection = actorSystem.actorSelection("/user/rule-" + rule.getRuleId() + "-dest-*");
        actorSelection.tell(new DriverDataMsg(result), ActorRef.noSender());
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
