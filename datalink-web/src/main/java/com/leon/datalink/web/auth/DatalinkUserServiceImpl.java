

package com.leon.datalink.web.auth;

import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.rule.script.Script;
import com.leon.datalink.web.exception.AccessException;
import com.leon.datalink.web.model.Page;
import com.leon.datalink.web.model.User;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.security.DatalinkUser;
import com.leon.datalink.web.security.DatalinkUserDetails;
import com.leon.datalink.web.security.JwtTokenManager;
import com.leon.datalink.web.util.RequestUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.leon.datalink.core.common.Constants.STORAGE_PATH;

/**
 * datalink user service.
 *
 * @author Leon
 */
@Service
public class DatalinkUserServiceImpl implements DatalinkUserService, UserDetailsService {

    /**
     * 资源列表
     */
    private final ConcurrentHashMap<String, User> userList = new ConcurrentHashMap<>();

    /**
     * key value storage
     */
    private final KvStorage kvStorage;

    @Autowired
    private JwtTokenManager tokenManager;

    @Autowired
    private AuthenticationManager authenticationManager;


    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String PARAM_USERNAME = "username";

    private static final String PARAM_PASSWORD = "password";

    private final static String USER_PATH = "/user";

    public DatalinkUserServiceImpl() throws Exception {

        // init storage
        this.kvStorage = new DatalinkKvStorage(STORAGE_PATH + USER_PATH);

        // read user list form storage
        if (this.kvStorage.allKeys().size() <= 0){
            // 初始化用户
            User user = new User();
            user.setUsername("datalink");
            user.setPassword("$2a$10$VxvrQ0Omo9ilSFjFwJKE5.7AVg0ug6.dMS.TVQBxbnuNkzuDDQdCS"); // aaaaaa
            this.kvStorage.put(user.getUsername().getBytes(), JacksonUtils.toJsonBytes(user));
            userList.put(user.getUsername(), user);
            return;
        }
        for (byte[] key : this.kvStorage.allKeys()) {
            String username = new String(key);
            byte[] value = this.kvStorage.get(key);
            User user = JacksonUtils.toObj(value, User.class);
            userList.put(username, user);
        }

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new DatalinkUserDetails(getUserByUsername(username));
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userList.get(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    @Override
    public void updateUserPassword(String username, String password) throws KvStorageException {
        User user = getUserByUsername(username);
        user.setPassword(password);
        userList.put(username, user);
        this.kvStorage.put(username.getBytes(), JacksonUtils.toJsonBytes(user));
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
