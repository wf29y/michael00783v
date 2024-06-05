

package com.leon.datalink.web.security;


import com.leon.datalink.web.model.User;

/**
 * Datalink User.
 *
 * @author Leon
 */
public class DatalinkUser extends User {
    
    private String token;
    
    private boolean globalAdmin = false;
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public boolean isGlobalAdmin() {
        return globalAdmin;
    }
    
    public void setGlobalAdmin(boolean globalAdmin) {
        this.globalAdmin = globalAdmin;
    }
    
    @Override
    public String toString() {
        return "DatalinkUser{" + "token='" + token + '\'' + ", globalAdmin=" + globalAdmin + '}';
    }
}
