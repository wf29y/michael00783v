

package com.leon.datalink.web.model;

import java.io.Serializable;

/**
 * User.
 *
 * @author Leon
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 3371769277802700069L;
    
    private String username;
    
    private String password;
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
