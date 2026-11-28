
package com.leon.datalink.web.service;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.transform.script.Script;
import com.leon.datalink.web.model.User;
import com.leon.datalink.web.exception.AccessException;

/**
 * User service.
 *
 * @author Leon
 */
public interface UserService extends BaseService<User> {

    void updateUserPassword(String username, String password) throws KvStorageException;

    String login(Object request) throws AccessException;

}
