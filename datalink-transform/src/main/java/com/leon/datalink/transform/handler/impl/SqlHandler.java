package com.leon.datalink.transform.handler.impl;

import cn.hutool.core.util.StrUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import org.jetlinks.reactor.ql.ReactorQL;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;

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

    private boolean isMatch(String id, String idFilter) {
        List<String> splitIds = StrUtil.split(id, '_');
        List<String> splitIdFilters = StrUtil.split(idFilter, '_');
        if (splitIds.size() >= splitIdFilters.size()) {
            StringBuilder newIdFilter = new StringBuilder();
            for (int i = 0; i < splitIdFilters.size(); i++) {
                String value = splitIdFilters.get(i);
                if (value.equals("+")) {
                    newIdFilter.append("+_");
                } else if (value.equals("#")) {
                    newIdFilter.append("#");
                    break;
                } else {
                    newIdFilter.append(splitIds.get(i)).append("_");
                }
            }
            newIdFilter = new StringBuilder(StrUtil.removeSuffix(newIdFilter.toString(), "_"));
            return idFilter.equals(newIdFilter.toString());
        } else return idFilter.equals(id);
    }

    @Override
    public void transform(RuntimeData runtimeData, Consumer<Object> consumer) {
        String entityRuntimeId = runtimeData.getEntityRuntimeId();
        reactorQL.start(name -> isMatch(entityRuntimeId, name) ?
                Flux.just(runtimeData.getData()) : Flux.just())
                .doOnNext(consumer).subscribe();
    }
}
