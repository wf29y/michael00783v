

package com.leon.datalink.web.auth;


import com.leon.datalink.web.model.Page;
import com.leon.datalink.web.model.User;
import com.leon.datalink.web.exception.AccessException;

import java.util.List;

/**
 * User CRUD service.
 *
 * @author Leon
 */
@SuppressWarnings("PMD.AbstractMethodOrInterfaceMethodMustUseJavadocRule")
public interface UserPersistService {

    /**
     * create user.
     *
     * @param username username
     * @param password password
     */
    void createUser(String username, String password);

    /**
     * delete user by username.
     *
     * @param username username
     */
    void deleteUser(String username);

    /**
     * update password of the user.
     *
     * @param username username
     * @param password password
     */
    void updateUserPassword(String username, String password);

    /**
     * query user by username.
     *
     * @param username username
     * @return user
     */
    User findUserByUsername(String username);

    /**
     * get users by page.
     *
     * @param pageNo pageNo
     * @param pageSize pageSize
     * @return user page info
     */
    Page<User> getUsers(int pageNo, int pageSize);

    /**
     * fuzzy query user by username.
     *
     * @param username username
     * @return usernames
     */
    List<String> findUserLikeUsername(String username);

    /**
     * Authentication of request, identify the user who request the resource.
     *
     * @param request where we can find the user information
     * @return user related to this request, null if no user info is found.
     * @throws AccessException if authentication is failed
     */
    User login(Object request) throws AccessException;
}
