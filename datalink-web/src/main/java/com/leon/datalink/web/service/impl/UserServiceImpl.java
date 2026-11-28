

package com.leon.datalink.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.PasswordEncoderUtil;
import com.leon.datalink.web.model.User;
import com.leon.datalink.web.exception.AccessException;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.security.DatalinkUserDetails;
import com.leon.datalink.web.security.JwtTokenManager;
import com.leon.datalink.web.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * datalink user service.
 *
 * @author Leon
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

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

    public UserServiceImpl() throws Exception {

        // init storage
        this.kvStorage = new DatalinkKvStorage(EnvUtil.getStoragePath() + USER_PATH);

        // read user list form storage
        if (this.kvStorage.allKeys().size() <= 0) {
            // 初始化用户
            User user = new User();
            user.setUsername("admin");
            user.setPassword(PasswordEncoderUtil.encode("datalink")); // aaaaaa
            user.setDescription("管理员");
            user.setPermissions(Lists.newArrayList("all"));
            user.setCreateTime(DateUtil.now());
            user.setSystem(true);
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
        return new DatalinkUserDetails(this.get(username));
    }

    @Override
    public void updateUserPassword(String username, String password) throws KvStorageException {
        User user = this.get(username);
        user.setPassword(password);
        userList.put(username, user);
        this.kvStorage.put(username.getBytes(), JacksonUtils.toJsonBytes(user));
    }


    @Override
    public String login(Object request) throws AccessException {
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

        return token;
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


    @Override
    public User get(String username) {
        User user = userList.get(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    @Override
    public void add(User user) throws KvStorageException {
        this.kvStorage.put(user.getUsername().getBytes(), JacksonUtils.toJsonBytes(user));
        userList.put(user.getUsername(), user);
    }

    @Override
    public void update(User user) throws KvStorageException {
        this.kvStorage.put(user.getUsername().getBytes(), JacksonUtils.toJsonBytes(user));
        userList.put(user.getUsername(), user);
    }

    @Override
    public void remove(String username) throws KvStorageException {
        this.kvStorage.delete(username.getBytes());
        userList.remove(username);
    }

    @Override
    public List<User> list(User user) {
        Stream<User> stream = userList.values().stream();
        if (null != user) {
            if (!StringUtils.isEmpty(user.getUsername())) {
                stream = stream.filter(s -> s.getUsername().contains(user.getUsername()));
            }
            if (!StringUtils.isEmpty(user.getDescription())) {
                stream = stream.filter(s -> s.getDescription().contains(user.getDescription()));
            }
        }
        return CollectionUtil.reverse(stream.sorted(Comparator.comparing(User::getCreateTime)).collect(Collectors.toList()));
    }

    @Override
    public int getCount(User user) {
        return this.list(user).size();
    }

}
