package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import com.alibaba.druid.pool.DruidDataSource;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class MysqlDriver extends AbstractDriver {

    private DruidDataSource dataSource;

    public MysqlDriver(Map<String, Object> properties) {
        super(properties);
    }

    public MysqlDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef) throws Exception {
        super(properties, driverMode, ruleActorRef);
    }

    @Override
    public void create() {
        DruidDataSource dataSource = new DruidDataSource(); // 创建Druid连接池
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver"); // 设置连接池的数据库驱动
        dataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT",
                getStrProp("ip"),
                getStrProp("port"),
                getStrProp("databaseName"))); // 设置数据库的连接地址
        dataSource.setUsername(getStrProp("username")); // 数据库的用户名
        dataSource.setPassword(getStrProp("password")); // 数据库的密码
        dataSource.setInitialSize(getIntProp("initSize", 8)); // 设置连接池的初始大小
        dataSource.setMinIdle(getIntProp("minIdle", 1)); // 设置连接池大小的下限
        dataSource.setMaxActive(getIntProp("maxActive", 20)); // 设置连接池大小的上限
        try {
            dataSource.getConnection();
        } catch (SQLException throwables) {
            return;
        }
        this.dataSource = dataSource;
    }

    @Override
    public void destroy() throws Exception {
        dataSource.close();
    }

    @Override
    public boolean test() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT",
                    getStrProp("ip"),
                    getStrProp("port"),
                    getStrProp("databaseName")),
                    getStrProp("username"),
                    getStrProp("password"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void handleData(Map data) throws Exception {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            if (connection != null) {
                String sql = getStrProp("sql");
                if (!StringUtils.isEmpty(sql)) {
                    String render = this.templateEngine.getTemplate(sql).render(data);
                    if (!StringUtils.isEmpty(render)) sql = render;
                }
                connection.createStatement().execute(sql);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            Objects.requireNonNull(connection).close();
        }
    }
}
