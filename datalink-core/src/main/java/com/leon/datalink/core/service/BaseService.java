package com.leon.datalink.core.service;

import com.leon.datalink.core.exception.KvStorageException;

import java.util.List;

public interface BaseService<T> {

    T get(String id);

    void add(T t) throws KvStorageException;

    void update(T t) throws KvStorageException;

    void remove(String id) throws KvStorageException;

    List<T> list(T t);

    int getCount(T t);
    
}
