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
            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                sendData(select());
            }, getLongProp("initialDelay"), getLongProp("period"), TimeUnit.valueOf(getStrProp("timeUnit")));
        }
    }


    private Object select() {
        Map<String, Object> variable = getVariable(null);
        Connection connection = null;
        String sql = null;
        List<Entity> result = null;
        try {
            connection = dataSource.getConnection();
            if (connection != null) {
                sql = getStrProp("sql");
                if (!StringUtils.isEmpty(sql)) {
                    String render = this.templateEngine.getTemplate(sql).render(variable);
                    if (!StringUtils.isEmpty(render)) sql = render;
                }
                result = SqlExecutor.query(connection, sql, new EntityListHandler());
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("pgsql driver error {}", e.getMessage());
        } finally {
            DbUtil.close(connection);
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
        Map<String, Object> variable = getVariable(data);

        Connection connection = null;
        String sql = null;
        Boolean result = null;
        try {
            connection = dataSource.getConnection();
            if (connection != null) {
                sql = getStrProp("sql");
                if (!StringUtils.isEmpty(sql)) {
                    String render = this.templateEngine.getTemplate(sql).render(variable);
                    if (!StringUtils.isEmpty(render)) sql = render;
                }
                result = connection.createStatement().execute(sql);
            }
        } finally {
            DbUtil.close(connection);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("result", result);
        return map;
    }
}
