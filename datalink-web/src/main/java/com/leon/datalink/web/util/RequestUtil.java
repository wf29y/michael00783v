

package com.leon.datalink.web.util;


import com.leon.datalink.web.model.User;
import org.apache.commons.lang3.StringUtils;
import com.leon.datalink.core.common.Constants;

import javax.servlet.http.HttpServletRequest;

/**
 * Request util.
 *
 * @author Leon
 */
public class RequestUtil {
    
    private static final String X_REAL_IP = "X-Real-IP";
    
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    
    private static final String X_FORWARDED_FOR_SPLIT_SYMBOL = ",";
    
    public static final String CLIENT_APPNAME_HEADER = "Client-AppName";
    
    public static final String DATALINK_USER_KEY = "datalinkuser";
    
    /**
     * get real client ip
     *
     * <p>first use X-Forwarded-For header    https://zh.wikipedia.org/wiki/X-Forwarded-For next nginx X-Real-IP last
     * {@link HttpServletRequest#getRemoteAddr()}
     *
     * @param request {@link HttpServletRequest}
     * @return remote ip address.
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (!StringUtils.isBlank(xForwardedFor)) {
            return xForwardedFor.split(X_FORWARDED_FOR_SPLIT_SYMBOL)[0].trim();
        }
        String nginxHeader = request.getHeader(X_REAL_IP);
        return StringUtils.isBlank(nginxHeader) ? request.getRemoteAddr() : nginxHeader;
    }
    
    /**
     * Gets the name of the client application in the header.
     *
     * @param request {@link HttpServletRequest}
     * @return may be return null
     */
    public static String getAppName(HttpServletRequest request) {
        return request.getHeader(CLIENT_APPNAME_HEADER);
    }
    
    /**
     * Gets the user of the client application in the Attribute.
     *
     * @param request {@link HttpServletRequest}
     * @return may be return null
     */
    public static User getUser(HttpServletRequest request) {
        Object userObj = request.getAttribute(DATALINK_USER_KEY);
        if (userObj == null) {
            return null;
        }
    
        return (User) userObj;
    }
    
    /**
     * Gets the username of the client application in the Attribute.
     *
     * @param request {@link HttpServletRequest}
     * @return may be return null
     */
    public static String getSrcUserName(HttpServletRequest request) {
        User user = getUser(request);
        // If auth is disabled, get username from parameters by agreed key
        return user == null ? request.getParameter(Constants.USERNAME) : user.getUsername();
    }
    
}
