package com.leon.datalink.rule.actor;

import akka.actor.*;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.datalink.core.common.GlobalVariableContent;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.actor.*;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.actor.RuntimeActor;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;
import com.leon.datalink.runtime.actor.RuntimeUpdateVarMsg;
import org.springframework.util.StringUtils;

import javax.script.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private final ActorSystem actorSystem = getContext().getSystem();

    private ActorRef runtimeActorRef;

    private ActorRef sourceActorRef;

    private List<ActorRef> destActorRefList;

    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");

    public RuleActor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public void preStart() {
        Loggers.RULE.info("rule [{}] actor start", rule.getRuleId());
        String ruleId = rule.getRuleId();
        // 创建runtime actor
        runtimeActorRef = actorSystem.actorOf((Props.create(RuntimeActor.class, rule.getRuleId(), rule.getVariables())), "runtime-" + rule.getRuleId());
        // 创建目的actor
        destActorRefList = rule.getDestResourceList().stream().map(destResource -> actorSystem.actorOf((Props.create(DriverActor.class, destResource.getResourceType().getDriver(), destResource.getProperties(), DriverModeEnum.DEST, this.getSelf(), rule.getRuleId())),
                String.format("rule-%s-dest-%s", ruleId, SnowflakeIdWorker.getId()))).collect(Collectors.toList());
        // 创建源actor
        Resource sourceResource = rule.getSourceResource();
        sourceActorRef = actorSystem.actorOf((Props.create(DriverActor.class, sourceResource.getResourceType().getDriver(), sourceResource.getProperties(), DriverModeEnum.SOURCE, this.getSelf(), rule.getRuleId())), String.format("rule-%s-source-%s", ruleId, SnowflakeIdWorker.getId()));
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
        Map<String, Object> data = msg.getData();

        Map<String, Object> result = null;
        boolean success = true;
        try {
            switch (rule.getAnalysisMode()) {
                case WITHOUT: {
                    result = data;
                    break;
                }
                case SCRIPT: {
                    result = scriptHandler(rule, data);
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
            runtimeActorRef.tell(new RuntimeUpdateDataMsg(data, success, new DateTime()), ActorRef.noSender());
        }
        // 忽略空值
        if (null == result && rule.isIgnoreNullValue()) return;

        // 发送给所有目的driver
        for (ActorRef actorRef : destActorRefList) {
            actorRef.tell(new DriverDataMsg(result), ActorRef.noSender());
        }
    }

    // 脚本解析
    private Map<String, Object> scriptHandler(Rule rule, Object data) {
        String script = rule.getScript();
        if (StringUtils.isEmpty(script) || null == data) {
            return null;
        }
        try {
            String ruleId = rule.getRuleId();

            // 获取并绑定自定义环境变量
            Bindings bind = scriptEngine.createBindings();

            Map<String, Object> globalVariable = GlobalVariableContent.get();
            for (String key : globalVariable.keySet()) {
                bind.put(key, globalVariable.get(key));
            }

            Map<String, Object> properties = RuntimeManger.getVariables(ruleId);
            for (String key : properties.keySet()) {
                bind.put(key, properties.get(key));
            }

            scriptEngine.setBindings(bind, ScriptContext.ENGINE_SCOPE);
            scriptEngine.eval(script);
            Invocable jsInvoke = (Invocable) scriptEngine;
            Object transform = jsInvoke.invokeFunction("transform", data);

            // 更新自定义环境变量
            if(MapUtil.isNotEmpty(properties)){
                properties.replaceAll((k, v) -> scriptEngine.getContext().getAttribute(k));
                runtimeActorRef.tell(new RuntimeUpdateVarMsg(properties), ActorRef.noSender());
            }
            return new ObjectMapper().convertValue(transform, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            Loggers.RULE.error("script error {}", e.getMessage());
        }
        return null;
    }


}
