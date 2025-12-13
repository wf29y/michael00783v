package com.leon.datalink.web.rule.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.rule.actor.RuleActor;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.transform.constants.TransformModeEnum;
import com.leon.datalink.transform.plugin.Plugin;
import com.leon.datalink.web.backup.BackupData;
import com.leon.datalink.web.plugin.PluginService;
import com.leon.datalink.web.resource.ResourceService;
import com.leon.datalink.web.rule.RuleService;
import com.leon.datalink.web.runtime.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
public class RuleServiceImpl implements RuleService, BackupData<Rule> {


    ActorSystem actorSystem;

    @Autowired
    @Lazy
    RuntimeService runtimeService;

    @Autowired
    @Lazy
    PluginService pluginService;

    @Autowired
    @Lazy
    ResourceService resourceService;

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
        runtimeService.initRuntime(rule);
    }

    @Override
    public void remove(String ruleId) throws KvStorageException {
        if (this.get(ruleId).isEnable()) this.stopRule(ruleId);
        this.kvStorage.delete(ruleId.getBytes());
        ruleList.remove(ruleId);
        runtimeService.remove(ruleId);
    }

    @Override
    public void update(Rule rule) throws KvStorageException {
        this.kvStorage.put(rule.getRuleId().getBytes(), JacksonUtils.toJsonBytes(rule));
        ruleList.put(rule.getRuleId(), rule);
        runtimeService.initRuntime(rule);
    }

    @Override
    public List<Rule> list(Rule rule) {
        Stream<Rule> stream = ruleList.values().stream();
        if (null != rule) {
            if (!StringUtils.isEmpty(rule.getRuleName())) {
                stream = stream.filter(r -> r.getRuleName().contains(rule.getRuleName()));
            }
            if (!StringUtils.isEmpty(rule.getSearchResourceId())) {
                stream = stream.filter(r -> {
                    String searchResourceId = rule.getSearchResourceId();
                    return r.getSourceResourceList().stream().anyMatch(resource -> searchResourceId.equals(resource.getResourceId()))
                            || r.getDestResourceList().stream().anyMatch(resource -> searchResourceId.equals(resource.getResourceId()));
                });
            }
            if (!StringUtils.isEmpty(rule.getSearchPluginId())) {
                stream = stream.filter(r -> {
                    String searchPluginId = rule.getSearchPluginId();
                    return r.getTransformList().stream().anyMatch(transform ->
                        TransformModeEnum.PLUGIN.equals(transform.getTransformMode())
                                && searchPluginId.equals(transform.getProperties().getObject("plugin", Plugin.class).getPluginId())
                    );
                });
            }
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    public int getCount(Rule rule) {
        return this.list(rule).size();
    }

    @Override
    public void startRule(String ruleId) throws Exception {
        Rule rule = this.get(ruleId);

        if (rule.isEnable()) return;

        // 修改为启动状态
        rule.setEnable(true);
        ruleList.put(ruleId, rule);

        // 创建rule actor
        ActorRef actorRef = actorSystem.actorOf((Props.create(RuleActor.class, rule)), "rule-" + ruleId);
        ruleActorRefList.put(ruleId, actorRef);

    }

    @Override
    public void stopRule(String ruleId) {
        Rule rule = this.get(ruleId);

        if (!rule.isEnable()) return;

        // 停止rule actor
        actorSystem.stop(ruleActorRefList.get(ruleId));
        ruleActorRefList.remove(ruleId);

        rule.setEnable(false);
        ruleList.put(ruleId, rule);
    }

    @Override
    public String dataKey() {
        return "rules";
    }

    @Override
    public List<Rule> createBackup() {
        return this.list(new Rule());
    }

    @Override
    public void recoverBackup(List<Rule> dataList) {
        try {
            List<Rule> list = this.list(new Rule());
            for (Rule rule : list) {
                this.remove(rule.getRuleId());
            }
            for (Rule rule : dataList) {
                this.add(rule);
            }
        } catch (KvStorageException e) {
            Loggers.WEB.error("recover rule backup error {}", e.getMessage());
        }
    }
}
