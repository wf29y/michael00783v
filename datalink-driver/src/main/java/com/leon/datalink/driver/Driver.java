package com.leon.datalink.driver;

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


    void handleMessage(Object message);


}
