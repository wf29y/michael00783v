package com.leon.datalink.rule.actor;

import akka.actor.*;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.actor.*;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.actor.RuntimeActor;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;
import org.springframework.util.StringUtils;

import javax.script.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private ActorRef runtimeActorRef;

    private List<ActorRef> destActorRefList;

    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");

    public RuleActor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public void preStart() {
        Loggers.RULE.info("start rule [{}]", getSelf().path());
        ActorContext context = getContext();
        // 创建runtime actor
        runtimeActorRef = context.actorOf((Props.create(RuntimeActor.class, rule.getRuleId(), new HashMap<>(rule.getVariables()))), "runtime");
        // 创建目的actor
        destActorRefList = rule.getDestResourceList().stream().map(destResource -> context.actorOf((Props.create(DriverActor.class, destResource.getResourceType().getDriver(), destResource.getProperties(), DriverModeEnum.DEST, rule.getRuleId())),
                "dest-" + SnowflakeIdWorker.getId())).collect(Collectors.toList());
        // 创建源actor
        Resource sourceResource = rule.getSourceResource();
        context.actorOf((Props.create(DriverActor.class, sourceResource.getResourceType().getDriver(), sourceResource.getProperties(), DriverModeEnum.SOURCE, rule.getRuleId())), "source");
    }

    @Override
    public void postStop() {
        Loggers.RULE.info("stop  rule [{}]", getSelf().path());
        // 子actor自动停止
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ReceiveDataMsg.class, this::transform).build();
    }

    private void transform(ReceiveDataMsg msg) {
        Object data = msg.getData();

        RuntimeUpdateDataMsg runtimeUpdateDataMsg = new RuntimeUpdateDataMsg();
        runtimeUpdateDataMsg.setReceiveData(data);
        runtimeUpdateDataMsg.setTime(DateTime.now());

        Object analysisData = null;
        try {
            switch (rule.getAnalysisMode()) {
                case WITHOUT: {
                    analysisData = data;
                    break;
                }
                case SCRIPT: {
                    analysisData = scriptHandler(rule, data);
                    break;
                }
                case JAR: {
                    // todo jar包解析
                    break;
                }
            }
            runtimeUpdateDataMsg.setAnalysisSuccess(true);
            runtimeUpdateDataMsg.setAnalysisData(analysisData);
        } catch (Exception e) {
            Loggers.DRIVER.error("analysis data error: {}", e.getMessage());
            runtimeUpdateDataMsg.setAnalysisSuccess(false);
            runtimeUpdateDataMsg.setMessage(e.getMessage());
            runtimeActorRef.tell(runtimeUpdateDataMsg, getSelf());
        }

        // 忽略空值
        if (null == analysisData && rule.isIgnoreNullValue()) {
            runtimeUpdateDataMsg.setMessage("ignore null value");
            runtimeActorRef.tell(runtimeUpdateDataMsg, getSelf());
            return;
        }

        // 发送给所有目的driver
        for (ActorRef actorRef : destActorRefList) {
            actorRef.tell(new PublishDataMsg(analysisData, runtimeActorRef, runtimeUpdateDataMsg), getSelf());
        }
    }

    // 脚本解析
    private Object scriptHandler(Rule rule, Object data) {
        String script = rule.getScript();
        if (StringUtils.isEmpty(script) || null == data) {
            return null;
        }
        try {
            String ruleId = rule.getRuleId();

            // 获取并绑定自定义环境变量
            Bindings bind = scriptEngine.createBindings();

            Map<String, Object> globalVariable = GlobalVariableContent.getAllValue();
            for (String key : globalVariable.keySet()) {
                bind.put(key, globalVariable.get(key));
            }

            Map<String, Object> variables = RuntimeManger.getVariables(ruleId);
            for (String key : variables.keySet()) {
                bind.put(key, variables.get(key));
            }

            scriptEngine.setBindings(bind, ScriptContext.ENGINE_SCOPE);
            scriptEngine.eval(script);
            Invocable jsInvoke = (Invocable) scriptEngine;
            Object transform = jsInvoke.invokeFunction("transform", data);

            // 更新自定义环境变量
            if (MapUtil.isNotEmpty(variables)) {
                variables.replaceAll((k, v) -> scriptEngine.getContext().getAttribute(k));
                // runtimeActorRef.tell(new RuntimeUpdateVarMsg(properties), getSelf());
            }
            return new ObjectMapper().convertValue(transform,Object.class);
        } catch (Exception e) {
            Loggers.RULE.error("script error {}", e.getMessage());
        }
        return null;
    }


}
