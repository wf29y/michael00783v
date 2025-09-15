package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SignUtil;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.entity.DriverProperties;
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


    @Override
    public void create(DriverModeEnum driverMode, DriverProperties properties) throws Exception {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(properties.getInteger("maxTotal", 8));
        config.setMaxIdle(properties.getInteger("maxIdle", 8));
        config.setMinIdle(properties.getInteger("minIdle", 4));
        config.setBlockWhenExhausted(false);

        String password = properties.getString("password");
        if (StringUtils.isEmpty(password)) {
            password = null;
        }
        int timeout = properties.getInteger("timeout", 6000);
        String mode = properties.getString("mode", "STANDALONE");

        if ("STANDALONE".equals(mode)) {
            String ip = properties.getString("ip");
            if (StringUtils.isEmpty(ip)) return;
            Integer port = properties.getInteger("port");
            if (null == port) return;
            jedisPool = new JedisPool(config, ip, port, timeout, password, properties.getInteger("database", 0));
        } else if ("CLUSTER".equals(mode)) {
            String nodes = properties.getString("nodes");
            if (StringUtils.isEmpty(nodes)) return;
            jedisCluster = new JedisCluster(nodesToHostAndPortSet(nodes), timeout, timeout, 3, password, config);
        } else {
            String nodes = properties.getString("nodes");
            if (StringUtils.isEmpty(nodes)) return;
            String masterName = properties.getString("masterName");
            if (StringUtils.isEmpty(masterName)) return;
            jedisPool = new JedisSentinelPool(masterName, nodesToNodeSet(nodes), config, timeout, password, properties.getInteger("database", 0));
        }

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            if (null == properties.getLong("initialDelay")) return;
            if (null == properties.getLong("period")) return;
            if (StringUtils.isEmpty(properties.getString("timeUnit"))) return;

            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> sendData(execute(null, properties)),
                    properties.getLong("initialDelay"), properties.getLong("period"), TimeUnit.valueOf(properties.getString("timeUnit")));
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
    public void destroy(DriverModeEnum driverMode, DriverProperties properties) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            executor.shutdown();
        }
        jedisPool.close();
    }

    @Override
    public boolean test(DriverProperties properties) {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(1);
            config.setMaxIdle(1);
            config.setMinIdle(1);
            config.setTestOnCreate(true);
            config.setTestOnBorrow(true);

            String password = properties.getString("password");
            if (StringUtils.isEmpty(password)) {
                password = null;
            }
            int timeout = properties.getInteger("timeout", 6000);
            String mode = properties.getString("mode", "STANDALONE");

            if ("STANDALONE".equals(mode)) {
                String ip = properties.getString("ip");
                if (StringUtils.isEmpty(ip)) return false;
                Integer port = properties.getInteger("port");
                if (null == port) return false;
                new JedisPool(config, ip, port, timeout, password, properties.getInteger("database", 0)).getResource();
            } else if ("CLUSTER".equals(mode)) {
                String nodes = properties.getString("nodes");
                if (StringUtils.isEmpty(nodes)) return false;
                new JedisCluster(nodesToHostAndPortSet(nodes), timeout, timeout, 3, password, config);
            } else {
                String nodes = properties.getString("nodes");
                if (StringUtils.isEmpty(nodes)) return false;
                String masterName = properties.getString("masterName");
                if (StringUtils.isEmpty(masterName)) return false;
                new JedisSentinelPool(masterName, nodesToNodeSet(nodes), config, timeout, password, properties.getInteger("database", 0)).getResource();
            }
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("redis test error{}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data, DriverProperties properties) throws Exception {
        return execute(data, properties);
    }

    private Object execute(Object data, DriverProperties properties) {

        String command = properties.getString("command");
        if (StringUtils.isEmpty(command)) return null;

        // 命令模板解析
        Map<String, Object> variable = getVariable(data);
        String render = this.templateAnalysis(command, variable);
        if (!StringUtils.isEmpty(render)) command = render;

        // 执行命令
        String mode = properties.getString("mode", "STANDALONE");
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
        map.put("driver", properties);
        return map;
    }

}
