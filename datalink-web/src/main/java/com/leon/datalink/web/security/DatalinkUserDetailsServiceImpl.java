

package com.leon.datalink.web.security;

import com.leon.datalink.web.auth.UserPersistService;
import com.leon.datalink.web.model.Page;
import com.leon.datalink.web.model.User;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.web.config.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * datalink user service.
 *
 * @author Leon
 */
@Service
public class DatalinkUserDetailsServiceImpl implements UserDetailsService {
    
    private Map<String, User> userMap = new ConcurrentHashMap<>();
    
    @Autowired
    private UserPersistService userPersistService;
    
    @Autowired
    private AuthConfig authConfig;
    
    @Scheduled(initialDelay = 5000, fixedDelay = 15000)
    private void reload() {
        try {
            Page<User> users = getUsersFromDatabase(1, Integer.MAX_VALUE);
            if (users == null) {
                return;
            }
            
            Map<String, User> map = new ConcurrentHashMap<>(16);
            for (User user : users.getPageItems()) {
                map.put(user.getUsername(), user);
            }
            userMap = map;
        } catch (Exception e) {
            Loggers.WEB.warn("[LOAD-USERS] load failed", e);
        }
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userMap.get(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new DatalinkUserDetails(user);
    }
    
    public void updateUserPassword(String username, String password) {
        userPersistService.updateUserPassword(username, password);
    }
    
    public Page<User> getUsersFromDatabase(int pageNo, int pageSize) {
        return userPersistService.getUsers(pageNo, pageSize);
    }
    
    public User getUser(String username) {
        User user = userMap.get(username);
        return user;
    }
    
    public User getUserFromDatabase(String username) {
        return userPersistService.findUserByUsername(username);
    }

    public List<String> findUserLikeUsername(String username) {
        return userPersistService.findUserLikeUsername(username);
    }

    public void createUser(String username, String password) {
        userPersistService.createUser(username, password);
    }
    
    public void deleteUser(String username) {
        userPersistService.deleteUser(username);
    }
}
