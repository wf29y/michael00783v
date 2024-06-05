package com.leon.datalink.rule;

import com.leon.datalink.driver.Driver;

import java.util.List;

public class RuleContent {

    private Rule rule;

    private Driver sourceDriver;

    private List<Driver> destDriverList;

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

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
