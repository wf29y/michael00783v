package com.leon.datalink.web.resource;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.resource.Resource;

import java.util.List;

public interface ResourceService {

    void addResource(Resource resource)  throws KvStorageException;

    void updateResource(Resource resource) throws KvStorageException;

    void removeResource(Resource resource) throws KvStorageException;

    List<Resource> listResource(Resource resource);

    int getResourceCount(Resource resource);

}
