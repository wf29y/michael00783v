package com.leon.datalink.web.rule.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.rule.actor.RuleActor;
import com.leon.datalink.rule.constants.TransformModeEnum;
import com.leon.datalink.rule.entity.Plugin;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.plugin.PluginService;
import com.leon.datalink.web.rule.RuleService;
import com.leon.datalink.web.runtime.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.leon.datalink.core.common.Constants.STORAGE_PATH;

/**
 * @ClassName RuleServiceImpl
 * @Description
 * @Author Leon
 * @Date2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class RuleServiceImpl implements RuleService {


    ActorSystem actorSystem;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    PluginService pluginService;

    /**
     * 资源列表
     */
    private final ConcurrentHashMap<String, Rule> ruleList = new ConcurrentHashMap<>();


    private final ConcurrentHashMap<String, ActorRef> ruleActorRefList = new ConcurrentHashMap<>();

    /**
     * key value storage
     */
    private final KvStorage kvStorage;

    /**
     * 规则持久化路径
     */
    private final static String RULE_PATH = "/rule";

    public RuleServiceImpl(ActorSystem actorSystem) throws Exception {

        this.actorSystem = actorSystem;

        // init storage
        this.kvStorage = new DatalinkKvStorage(STORAGE_PATH + RULE_PATH);

        // read resource list form storage
        if (this.kvStorage.allKeys().size() <= 0) return;
        for (byte[] key : this.kvStorage.allKeys()) {
            String ruleId = new String(key);
            byte[] value = this.kvStorage.get(key);
            Rule rule = JacksonUtils.toObj(value, Rule.class);
            ruleList.put(ruleId, rule);
        }

    }

    @Override
    public Rule get(String ruleId) {
        return ruleList.get(ruleId);
    }

    @Override
    public void add(Rule rule) throws KvStorageException {
        if (StringUtils.isEmpty(rule.getRuleId())) rule.setRuleId(SnowflakeIdWorker.getId());
        this.kvStorage.put(rule.getRuleId().getBytes(), JacksonUtils.toJsonBytes(rule));
        ruleList.put(rule.getRuleId(), rule);
    }

    @Override
    public void remove(Rule rule) throws KvStorageException {
        this.kvStorage.delete(rule.getRuleId().getBytes());
        ruleList.remove(rule.getRuleId());
        runtimeService.remove(rule.getRuleId());
    }

    @Override
    public void update(Rule rule) throws KvStorageException {
        this.kvStorage.put(rule.getRuleId().getBytes(), JacksonUtils.toJsonBytes(rule));
        ruleList.put(rule.getRuleId(), rule);
    }

    @Override
    public List<Rule> list(Rule rule) {
        Stream<Rule> stream = ruleList.values().stream();
        if (null != rule) {
            if (!StringUtils.isEmpty(rule.getRuleName())) {
                stream = stream.filter(r -> r.getRuleName().contains(rule.getRuleName()));
            }
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    public int getCount(Rule rule) {
        return this.list(rule).size();
    }

    @Override
    public void startRule(Rule rule) throws Exception {
        String ruleId = rule.getRuleId();

        // 修改为启动状态
        rule.setEnable(true);
        ruleList.put(ruleId, rule);

        // 查询插件
        if(TransformModeEnum.PLUGIN.equals(rule.getTransformMode())){
            Plugin plugin = pluginService.get(rule.getPluginId());
            rule.setPlugin(plugin);
        }

        // 创建rule actor
        ActorRef actorRef = actorSystem.actorOf((Props.create(RuleActor.class, rule)), "rule-" + ruleId);
        ruleActorRefList.put(ruleId, actorRef);

    }

    @Override
    public void stopRule(Rule rule) {
        String ruleId = rule.getRuleId();
        // 停止rule actor
        actorSystem.stop(ruleActorRefList.get(ruleId));
        ruleActorRefList.remove(ruleId);

        rule.setEnable(false);
        ruleList.put(ruleId, rule);
    }

}
