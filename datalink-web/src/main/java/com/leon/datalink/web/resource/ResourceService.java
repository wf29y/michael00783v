package com.leon.datalink.web.resource;

import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.resource.Resource;

public interface ResourceService extends BaseService<Resource> {

    boolean testDriver(Resource resource) throws Exception;

}
