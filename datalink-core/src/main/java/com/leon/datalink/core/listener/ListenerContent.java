package com.leon.datalink.core.listener;

import java.util.*;

/**
 * 监听器
 */
public class ListenerContent {

    private static final Map<Integer, Listener> listeners = new HashMap<>();

//    static {
//        Integer port = Integer.parseInt(System.getProperty("server.port"));
//        System.out.println(port);
//        listeners.put(port, new Listener(port, ListenerTypeEnum.TCP, "系统端口"));
//    }

    public static void add(int port, ListenerTypeEnum type, String desc) {
        listeners.put(port, new Listener(port, type, desc));
    }

    public static Listener get(int port) {
        return listeners.get(port);
    }

    public static List<Listener> getList() {
        Collection<Listener> values = listeners.values();
        return new ArrayList<>(values);
    }

    public static void remove(int port) {
        listeners.remove(port);
    }


}
