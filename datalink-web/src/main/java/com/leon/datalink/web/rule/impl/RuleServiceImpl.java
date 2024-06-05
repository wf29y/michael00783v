package com.leon.datalink.web.rule.impl;

import akka.actor.ActorSystem;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.rule.IRuleEngine;
import com.leon.datalink.rule.Rule;
import com.leon.datalink.web.rule.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.leon.datalink.core.common.Constants.STORAGE_PATH;

/**
 * @ClassNameResourceManager
 * @Description
 * @Author Leon
 * @Date2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class RuleServiceImpl implements RuleService {
    /**
     * actor syatem
     */
    ActorSystem actorSystem;

    @Autowired
    private IRuleEngine ruleEngine;

    /**
     * 资源列表
     */
    private final ConcurrentHashMap<String, Rule> ruleList = new ConcurrentHashMap<>();

    /**
     * key value storage
     */
    private final KvStorage kvStorage;

    /**
     * 资源持久化路径
     */
    private final static String RULE_PATH = "/rule";

    public RuleServiceImpl(ActorSystem actorSystem) throws Exception {

        // actor system
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

//    @PostConstruct
//    public void init() {
//
//    }

    @Override
    public Rule getRule(String ruleId) {
       return ruleList.get(ruleId);
    }

    @Override
    public void addRule(Rule rule) throws KvStorageException {
        if (StringUtils.isEmpty(rule.getRuleId())) rule.setRuleId(SnowflakeIdWorker.getId());
        this.kvStorage.put(rule.getRuleId().getBytes(), JacksonUtils.toJsonBytes(rule));
        ruleList.put(rule.getRuleId(), rule);
    }

    @Override
    public void removeRule(Rule rule) throws KvStorageException {
        //todo 是否正在使用
        this.kvStorage.delete(rule.getRuleId().getBytes());
        ruleList.remove(rule.getRuleId());

    }

    @Override
    public void updateRule(Rule rule) throws KvStorageException {
        //todo 是否正在使用
        this.kvStorage.put(rule.getRuleId().getBytes(), JacksonUtils.toJsonBytes(rule));
        ruleList.put(rule.getRuleId(), rule);
    }

    @Override
    public List<Rule> listRule(Rule rule) {
        return new ArrayList<>(ruleList.values());
    }

    @Override
    public int getRuleCount(Rule rule) {
        return this.listRule(rule).size();
    }

    @Override
    public void startRule(Rule rule) throws Exception {
        ruleEngine.start(rule);
        rule.setEnable(true);
        this.updateRule(rule);
    }

    @Override
    public void stopRule(Rule rule) throws Exception {
        ruleEngine.stop(rule);
        rule.setEnable(false);
        this.updateRule(rule);
    }

}
