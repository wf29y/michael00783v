package com.leon.datalink.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.leon.datalink.core.common.Constants.LOCAL_IP_PROPERTY_KEY;

/**
 * @ClassNameMway
 * @Description
 * @Author Leon
 * @Date2022/3/29 15:57
 * @Version V1.0
 **/
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.leon.datalink")
@ServletComponentScan
public class Datalink {
    public static void main(String[] args) throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.setProperty(LOCAL_IP_PROPERTY_KEY, ip);
        SpringApplication.run(Datalink.class, args);

    }
}
