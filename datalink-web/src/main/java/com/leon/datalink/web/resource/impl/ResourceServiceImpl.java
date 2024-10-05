package com.leon.datalink.web.resource.impl;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverFactory;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.web.resource.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.leon.datalink.core.common.Constants.STORAGE_PATH;

/**
 * @ClassNameResourceManager
 * @Description
 * @Author Leon
 * @Date2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class ResourceServiceImpl implements ResourceService {

    /**
     * 资源列表
     */
    private final ConcurrentHashMap<String, Resource> resourceList = new ConcurrentHashMap<>();

    /**
     * key value storage
     */
    private final KvStorage kvStorage;

    /**
     * 资源持久化路径
     */
    private final static String RESOURCE_PATH = "/resource";

    public ResourceServiceImpl() throws Exception {

        // init storage
        this.kvStorage = new DatalinkKvStorage(STORAGE_PATH + RESOURCE_PATH);

        // read resource list form storage
        if (this.kvStorage.allKeys().size() <= 0) return;
        for (byte[] key : this.kvStorage.allKeys()) {
            String resourceId = new String(key);
            byte[] value = this.kvStorage.get(key);
            Resource resource = JacksonUtils.toObj(value, Resource.class);
            resourceList.put(resourceId, resource);
        }

    }

    @Override
    public Resource get(String resourceId) {
        return resourceList.get(resourceId);
    }

    @Override
    public void add(Resource resource) throws KvStorageException {
        if (StringUtils.isEmpty(resource.getResourceId())) resource.setResourceId(SnowflakeIdWorker.getId());
        this.kvStorage.put(resource.getResourceId().getBytes(), JacksonUtils.toJsonBytes(resource));
        resourceList.put(resource.getResourceId(), resource);
    }

    @Override
    public void remove(Resource resource) throws KvStorageException {
        this.kvStorage.delete(resource.getResourceId().getBytes());
        resourceList.remove(resource.getResourceId());
    }

    @Override
    public void update(Resource resource) throws KvStorageException {
        this.kvStorage.put(resource.getResourceId().getBytes(), JacksonUtils.toJsonBytes(resource));
        resourceList.put(resource.getResourceId(), resource);
    }

    @Override
    public List<Resource> list(Resource resource) {
        Stream<Resource> stream = resourceList.values().stream();
        if (null != resource) {
            if (null != resource.getResourceType()) {
                stream = stream.filter(r -> r.getResourceType().equals(resource.getResourceType()));
            }
            if (!StringUtils.isEmpty(resource.getResourceName())) {
                stream = stream.filter(r -> r.getResourceName().contains(resource.getResourceName()));
            }
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    public int getCount(Resource resource) {
        return this.list(resource).size();
    }

    @Override
    public boolean testDriver(Resource resource) throws Exception {
        Driver driver = DriverFactory.getDriver(resource.getResourceType().getDriver(), resource.getProperties());
        return driver.test();
    }

}
