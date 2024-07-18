package com.leon.datalink.driver;

import java.util.Map;

/**
 * @ClassName Driver
 * @Description
 * @Author Solley
 * @Date 2022/4/8 15:10
 * @Version V1.0
 **/
public interface Driver {

    /**
     * create driver
     */
    void create();

    /**
     * destroy client
     */
    void destroy() throws Exception;


    void handleData(Map data) throws Exception;


}
