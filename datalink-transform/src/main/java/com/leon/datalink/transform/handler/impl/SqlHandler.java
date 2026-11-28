package com.leon.datalink.transform.handler.impl;

import com.leon.datalink.core.utils.MatchUtil;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import org.jetlinks.reactor.ql.ReactorQL;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

public class SqlHandler implements TransformHandler {

    ReactorQL reactorQL;

    MatchUtil matchUtil;

    @Override
    public void init(Transform transform) {
        matchUtil = new MatchUtil("_");

        String sql = transform.getProperties().getString("sql");
        reactorQL = ReactorQL.builder()
                .sql(sql)
                .build();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void transform(RuntimeData runtimeData, Consumer<Object> consumer) {
        String entityRuntimeId = runtimeData.getEntityRuntimeId();
        reactorQL.start(name -> matchUtil.isMatch(entityRuntimeId, name) ?
                Flux.just(runtimeData.getData()) : Flux.just())
                .doOnNext(consumer).subscribe();
    }
}
