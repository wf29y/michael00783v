package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SignUtil;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.util.RedisClusterCmd;
import com.leon.datalink.driver.util.RedisCmd;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RedisDriver extends AbstractDriver {

    private JedisPoolAbstract jedisPool;

    private JedisCluster jedisCluster;

    private ScheduledExecutorService executor;

    public RedisDriver(Map<String, Object> properties) {
        super(properties);
    }

    public RedisDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        super(properties, driverMode, ruleActorRef, ruleId);
    }

    @Override
    public void create() throws Exception {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(getIntProp("maxTotal", 8));
        config.setMaxIdle(getIntProp("maxIdle", 8));
        config.setMinIdle(getIntProp("minIdle", 4));
        config.setBlockWhenExhausted(false);

        String password = getStrProp("password");
        if (StringUtils.isEmpty(password)) {
            password = null;
        }
        int timeout = getIntProp("timeout", 6000);
        String mode = getStrProp("mode", "STANDALONE");

        if ("STANDALONE".equals(mode)) {
            String ip = getStrProp("ip");
            int port = getIntProp("port");
            int database = getIntProp("database", 0);
            jedisPool = new JedisPool(config, ip, port, timeout, password, database);
        } else if ("CLUSTER".equals(mode)) {
            Set<HostAndPort> hostAndPortSet = nodesToHostAndPortSet(getStrProp("nodes"));
            jedisCluster = new JedisCluster(hostAndPortSet, timeout, timeout, 3, password, config);
        } else {
            Set<String> nodeSet = nodesToNodeSet(getStrProp("nodes"));
            String masterName = getStrProp("masterName");
            int database = getIntProp("database", 0);
            jedisPool = new JedisSentinelPool(masterName, nodeSet, config, timeout, password, database);
        }

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> sendData(execute(null)),
                    getLongProp("initialDelay"), getLongProp("period"), TimeUnit.valueOf(getStrProp("timeUnit")));
        }

    }

    public static Set<String> nodesToNodeSet(String nodes) {
        String[] nodeList = SignUtil.splitByCommas(nodes);
        HashSet<String> result = new HashSet<>();
        Collections.addAll(result, nodeList);
        return result;
    }

    public static Set<HostAndPort> nodesToHostAndPortSet(String nodes) {
        String[] nodeList = SignUtil.splitByCommas(nodes);
        int length = nodeList.length;
        Set<HostAndPort> hostAndPortSet = new HashSet<>(length);
        if (length > 0) {
            for (String node : nodeList) {
                String[] ipAndPort = SignUtil.splitByColon(node);
                HostAndPort hostAndPort = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                hostAndPortSet.add(hostAndPort);
            }
        }
        return hostAndPortSet;
    }


    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            executor.shutdown();
        }
        jedisPool.close();
    }

    @Override
    public boolean test() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(1);
            config.setMaxIdle(1);
            config.setMinIdle(1);
            config.setTestOnCreate(true);
            config.setTestOnBorrow(true);

            String password = getStrProp("password");
            if (StringUtils.isEmpty(password)) {
                password = null;
            }
            int timeout = getIntProp("timeout", 6000);
            String mode = getStrProp("mode", "STANDALONE");

            if ("STANDALONE".equals(mode)) {
                String ip = getStrProp("ip");
                int port = getIntProp("port");
                int database = getIntProp("database", 0);
                new JedisPool(config, ip, port, timeout, password, database).getResource();
            } else if ("CLUSTER".equals(mode)) {
                Set<HostAndPort> hostAndPortSet = nodesToHostAndPortSet(getStrProp("nodes"));
                new JedisCluster(hostAndPortSet, timeout, timeout, 3, password, config);
            } else {
                Set<String> nodeSet = nodesToNodeSet(getStrProp("nodes"));
                String masterName = getStrProp("masterName");
                int database = getIntProp("database", 0);
                new JedisSentinelPool(masterName, nodeSet, config, timeout, password, database).getResource();
            }
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("redis test error{}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data) throws Exception {
        return execute(data);
    }

    private Object execute(Object data) {
        Map<String, Object> variable = getVariable(data);

        // 命令模板解析
        String command = getStrProp("command");
        if (!StringUtils.isEmpty(command)) {
            String render = this.templateEngine.getTemplate(command).render(variable);
            if (!StringUtils.isEmpty(render)) command = render;
        }

        // 执行命令
        String mode = getStrProp("mode", "STANDALONE");
        Object result = null;
        if ("CLUSTER".equals(mode)) {
            result = RedisClusterCmd.execute(jedisCluster, command);
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                result = RedisCmd.execute(jedis, command);
            } catch (Exception e) {
                Loggers.DRIVER.error("redis execute command error {}", e.getMessage());
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("command", command);
        map.put("result", result);
        return map;
    }

}
