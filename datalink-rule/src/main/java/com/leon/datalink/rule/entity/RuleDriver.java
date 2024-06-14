package com.leon.datalink.rule.entity;

import cn.hutool.core.date.DateTime;
import com.leon.datalink.driver.Driver;

import java.util.List;

public class RuleDriver {

    private Driver sourceDriver;

    private List<Driver> destDriverList;

    public Driver getSourceDriver() {
        return sourceDriver;
    }

    public void setSourceDriver(Driver sourceDriver) {
        this.sourceDriver = sourceDriver;
    }

    public List<Driver> getDestDriverList() {
        return destDriverList;
    }

    public void setDestDriverList(List<Driver> destDriverList) {
        this.destDriverList = destDriverList;
    }

}
