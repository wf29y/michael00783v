package com.leon.datalink.driver.impl;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimescaleDBDriver extends AbstractDriver {

    private DruidDataSource dataSource;

    private ScheduledExecutorService executor;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("databaseName"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("username"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("password"))) throw new ValidateException();


        DruidDataSource dataSource = new DruidDataSource(); // 创建Druid连接池
        dataSource.setDriverClassName("org.postgresql.Driver"); // 设置连接池的数据库驱动
        dataSource.setUrl(String.format("jdbc:postgresql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT",
                properties.getString("ip"),
                properties.getString("port"),
                properties.getString("databaseName"))); // 设置数据库的连接地址
        dataSource.setUsername(properties.getString("username")); // 数据库的用户名
        dataSource.setPassword(properties.getString("password")); // 数据库的密码
        dataSource.setInitialSize(properties.getInteger("initSize", 8)); // 设置连接池的初始大小
        dataSource.setMinIdle(properties.getInteger("minIdle", 1)); // 设置连接池大小的下限
        dataSource.setMaxActive(properties.getInteger("maxActive", 20)); // 设置连接池大小的上限
        dataSource.setValidationQuery("select version();");
        this.dataSource = dataSource;


        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            if (null == properties.getLong("initialDelay")) throw new ValidateException();
            if (null == properties.getLong("period")) throw new ValidateException();
            if (StringUtils.isEmpty(properties.getString("timeUnit"))) throw new ValidateException();

            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                try {
                    produceData(select(properties));
                } catch (Exception e) {
                    produceDataError(e.getMessage());
                }
            }, properties.getLong("initialDelay"), properties.getLong("period"), TimeUnit.valueOf(properties.getString("timeUnit")));
        }
    }


    private Object select(ConfigProperties properties) throws Exception {
        String sql = properties.getString("sql");
        if (StringUtils.isEmpty(sql)) throw new ValidateException();

        List<Entity> result = null;
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                String render = this.templateAnalysis(sql, getVariable(null));
                if (!StringUtils.isEmpty(render)) sql = render;
                result = SqlExecutor.query(connection, sql, new EntityListHandler());
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("timescaleDB driver error {}", e.getMessage());
            throw e;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("result", result);
        map.put("driver", properties);
        return map;
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            executor.shutdown();
        }
        dataSource.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) return false;
        if (StringUtils.isEmpty(properties.getString("port"))) return false;
        if (StringUtils.isEmpty(properties.getString("databaseName"))) return false;
        if (StringUtils.isEmpty(properties.getString("username"))) return false;
        if (StringUtils.isEmpty(properties.getString("password"))) return false;

        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.getConnection(String.format("jdbc:postgresql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT",
                    properties.getString("ip"),
                    properties.getString("port"),
                    properties.getString("databaseName")),
                    properties.getString("username"),
                    properties.getString("password"));
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("timescaleDB driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        String sql = properties.getString("sql");
        if (StringUtils.isEmpty(sql)) throw new ValidateException();

        Boolean result = null;
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                String render = this.templateAnalysis(sql, getVariable(data));
                if (!StringUtils.isEmpty(render)) sql = render;
                result = connection.createStatement().execute(sql);
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("timescaleDB driver error {}", e.getMessage());
            throw e;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("result", result);
        return map;
    }
}
