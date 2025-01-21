package com.leon.datalink.driver;

import java.util.Map;

/**
 * @ClassName Driver
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:10
 * @Version V1.0
 **/
public interface Driver {

    /**
     * create driver
     */
    void create() throws Exception;

    /**
     * destroy client
     */
    void destroy() throws Exception;

    /**
     * test ok
     */
    boolean test();

    /**
     * handle receive data
     */
    Object handleData(Object data) throws Exception;


}
