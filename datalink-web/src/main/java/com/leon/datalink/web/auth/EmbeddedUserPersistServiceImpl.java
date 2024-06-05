
package com.leon.datalink.web.auth;


import com.leon.datalink.web.model.Page;
import com.leon.datalink.web.model.User;
import com.leon.datalink.web.util.RequestUtil;
import com.leon.datalink.web.security.DatalinkUser;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.web.exception.AccessException;
import com.leon.datalink.web.security.JwtTokenManager;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * 嵌入用户管理
 *
 * @author Leon
 */
@Service
public class EmbeddedUserPersistServiceImpl implements UserPersistService {

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String PARAM_USERNAME = "username";

    private static final String PARAM_PASSWORD = "password";

    @Autowired
    private JwtTokenManager tokenManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void createUser(String username, String password) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void updateUserPassword(String username, String password) {

    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public Page<User> getUsers(int pageNo, int pageSize) {
        Page<User> userPage = new Page<>();
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUsername("datalink");
        user.setPassword("$2a$10$VxvrQ0Omo9ilSFjFwJKE5.7AVg0ug6.dMS.TVQBxbnuNkzuDDQdCS");
        users.add(user);
        userPage.setPageItems(users);
        userPage.setTotalCount(1);
        return userPage;
    }

    @Override
    public List<String> findUserLikeUsername(String username) {
        return null;
    }

    @Override
    public User login(Object request) throws AccessException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = resolveToken(req);
        if (StringUtils.isBlank(token)) {
            throw new AccessException("user not found!");
        }

        try {
            tokenManager.validateToken(token);
        } catch (ExpiredJwtException e) {
            throw new AccessException("token expired!");
        } catch (Exception e) {
            throw new AccessException("token invalid!");
        }

        Authentication authentication = tokenManager.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();
        DatalinkUser user = new DatalinkUser();
        user.setUsername(username);
        user.setToken(token);
        req.setAttribute(RequestUtil.DATALINK_USER_KEY, user);
        return user;
    }

    /**
     * Get token from header.
     */
    private String resolveToken(HttpServletRequest request) throws AccessException {
        String bearerToken = request.getHeader(DatalinkAuthConfig.AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        bearerToken = request.getParameter(Constants.ACCESS_TOKEN);
        if (StringUtils.isBlank(bearerToken)) {
            String userName = request.getParameter(PARAM_USERNAME);
            String password = request.getParameter(PARAM_PASSWORD);
            bearerToken = resolveTokenFromUser(userName, password);
        }

        return bearerToken;
    }

    private String resolveTokenFromUser(String userName, String rawPassword) throws AccessException {
        String finalName;
        Authentication authenticate;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName,
                    rawPassword);
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new AccessException("unknown user!");
        }

        if (null == authenticate || StringUtils.isBlank(authenticate.getName())) {
            finalName = userName;
        } else {
            finalName = authenticate.getName();
        }

        return tokenManager.createToken(finalName);
    }
}
