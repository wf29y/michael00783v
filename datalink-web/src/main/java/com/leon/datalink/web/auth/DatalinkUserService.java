
package com.leon.datalink.web.auth;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.web.exception.AccessException;
import com.leon.datalink.web.model.User;

/**
 * User service.
 *
 * @author Leon
 */
public interface DatalinkUserService {

    User getUserByUsername(String username);

    void updateUserPassword(String username, String password) throws KvStorageException;

    User login(Object request) throws AccessException;
}
