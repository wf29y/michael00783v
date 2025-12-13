package com.leon.datalink.transform.handler.impl;

import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import org.jetlinks.reactor.ql.ReactorQL;

public class SqlHandler implements TransformHandler {

    ReactorQL reactorQL;

    @Override
    public void init(Transform transform) {
        String sql = transform.getProperties().getString("sql");
        reactorQL = ReactorQL.builder()
                .sql(sql)
                .build();
    }

    @Override
    public void destroy() {
    }

    @Override
    public Object transform(Object data) {
//        reactorQL.start(data)
//                .doOnNext(map -> {
//
//                }).subscribe();
        return data;
    }
}
