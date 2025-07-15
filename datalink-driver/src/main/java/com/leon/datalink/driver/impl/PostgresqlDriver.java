package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PostgresqlDriver extends AbstractDriver {

    private DruidDataSource dataSource;

    private ScheduledExecutorService executor;

    public PostgresqlDriver(Map<String, Object> properties) {
        super(properties);
    }

    public PostgresqlDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        super(properties, driverMode, ruleActorRef, ruleId);
    }

    @Override
    public void create() throws Exception {
        if (StringUtils.isEmpty(getStrProp("ip"))) return;
        if (StringUtils.isEmpty(getStrProp("port"))) return;
        if (StringUtils.isEmpty(getStrProp("databaseName"))) return;
        if (StringUtils.isEmpty(getStrProp("username"))) return;
        if (StringUtils.isEmpty(getStrProp("password"))) return;

        try {
            DruidDataSource dataSource = new DruidDataSource(); // 创建Druid连接池
            dataSource.setDriverClassName("org.postgresql.Driver"); // 设置连接池的数据库驱动
            dataSource.setUrl(String.format("jdbc:postgresql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT",
                    getStrProp("ip"),
                    getStrProp("port"),
                    getStrProp("databaseName"))); // 设置数据库的连接地址
            dataSource.setUsername(getStrProp("username")); // 数据库的用户名
            dataSource.setPassword(getStrProp("password")); // 数据库的密码
            dataSource.setInitialSize(getIntProp("initSize", 8)); // 设置连接池的初始大小
            dataSource.setMinIdle(getIntProp("minIdle", 1)); // 设置连接池大小的下限
            dataSource.setMaxActive(getIntProp("maxActive", 20)); // 设置连接池大小的上限
            dataSource.setValidationQuery("select version();");
            this.dataSource = dataSource;
        } catch (Exception throwables) {
            return;
        }

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            if (null == getLongProp("initialDelay")) return;
            if (null == getLongProp("period")) return;
            if (StringUtils.isEmpty(getStrProp("timeUnit"))) return;

            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                sendData(select());
            }, getLongProp("initialDelay"), getLongProp("period"), TimeUnit.valueOf(getStrProp("timeUnit")));
        }
    }


    private Object select() {
        String sql = getStrProp("sql");
        if (StringUtils.isEmpty(sql)) return null;

        List<Entity> result = null;
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                String render = this.templateEngine.getTemplate(sql).render(getVariable(null));
                if (!StringUtils.isEmpty(render)) sql = render;
                result = SqlExecutor.query(connection, sql, new EntityListHandler());
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("pgsql driver error {}", e.getMessage());
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("result", result);
        return map;
    }

    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            executor.shutdown();
        }
        dataSource.close();
    }

    @Override
    public boolean test() {
        if (StringUtils.isEmpty(getStrProp("ip"))) return false;
        if (StringUtils.isEmpty(getStrProp("port"))) return false;
        if (StringUtils.isEmpty(getStrProp("databaseName"))) return false;
        if (StringUtils.isEmpty(getStrProp("username"))) return false;
        if (StringUtils.isEmpty(getStrProp("password"))) return false;

        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.getConnection(String.format("jdbc:postgresql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT",
                    getStrProp("ip"),
                    getStrProp("port"),
                    getStrProp("databaseName")),
                    getStrProp("username"),
                    getStrProp("password"));
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("pgsql driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data) throws Exception {
        String sql = getStrProp("sql");
        if (StringUtils.isEmpty(sql)) return null;

        Boolean result = null;
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                String render = this.templateEngine.getTemplate(sql).render(getVariable(data));
                if (!StringUtils.isEmpty(render)) sql = render;
                result = connection.createStatement().execute(sql);
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("pgsql driver error {}", e.getMessage());
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("result", result);
        return map;
    }
}
