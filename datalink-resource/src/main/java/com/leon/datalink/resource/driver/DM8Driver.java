package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;

public class DM8Driver extends AbstractDriver {

    private DruidDataSource dataSource;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("username"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("password"))) throw new ValidateException();

        DruidDataSource dataSource = new DruidDataSource(); // 创建Druid连接池
        dataSource.setDriverClassName("dm.jdbc.driver.DmDriver"); // 设置连接池的数据库驱动
        dataSource.setUrl(String.format("jdbc:dm://%s:%s", properties.getString("ip"), properties.getString("port"))); // 设置数据库的连接地址
        dataSource.setUsername(properties.getString("username")); // 数据库的用户名
        dataSource.setPassword(properties.getString("password")); // 数据库的密码
        dataSource.setInitialSize(properties.getInteger("initSize", 8)); // 设置连接池的初始大小
        dataSource.setMinIdle(properties.getInteger("minIdle", 1)); // 设置连接池大小的下限
        dataSource.setMaxActive(properties.getInteger("maxActive", 20)); // 设置连接池大小的上限
        dataSource.setValidationQuery("select 1;");
        this.dataSource = dataSource;
    }


    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        dataSource.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) return false;
        if (StringUtils.isEmpty(properties.getString("port"))) return false;
        if (StringUtils.isEmpty(properties.getString("username"))) return false;
        if (StringUtils.isEmpty(properties.getString("password"))) return false;

        try {
            Class.forName("dm.jdbc.driver.DmDriver");
            DriverManager.getConnection(String.format("jdbc:dm://%s:%s", properties.getString("ip"), properties.getString("port")),
                    properties.getString("username"),
                    properties.getString("password"));
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("dm8 driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void scheduleTrigger(ConfigProperties properties) throws Exception {
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
            Loggers.DRIVER.error("dm8 driver error {}", e.getMessage());
            throw e;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("result", result);
        map.put("driver", properties);
        produceData(map);
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
            Loggers.DRIVER.error("dm8 driver error {}", e.getMessage());
            throw e;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("result", result);
        return map;
    }
}
