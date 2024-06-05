package com.leon.datalink.driver.snmp;

import org.snmp4j.mp.SnmpConstants;

/**
 * @ClassNameConstants
 * @Description
 * @Author Leon
 * @Date2022/3/31 9:42
 * @Version V1.0
 **/
public class Constants {
    public static final int DEFAULT_VERSION = SnmpConstants.version2c;
    public static final String DEFAULT_PROTOCOL = "udp";
    public static final int DEFAULT_PORT = 161;
    public static final long DEFAULT_TIMEOUT = 3 * 1000L;
    public static final int DEFAULT_RETRY = 3;
}
