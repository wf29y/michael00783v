

package com.leon.datalink.web.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leon.datalink.web.security.DatalinkUser;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.web.auth.UserPersistService;
import com.leon.datalink.web.config.AuthConfig;
import com.leon.datalink.web.exception.AccessException;
import com.leon.datalink.web.security.JwtTokenManager;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.security.DatalinkUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User related methods entry.
 *
 * @author Leon
 */
@RestController
@RequestMapping({"/v1/auth", "/v1/auth/users"})
public class UserController {

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DatalinkUserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserPersistService authService;

    /**
     * Get user info.
     *
     * @return Current login user info
     */
    @GetMapping("/info")
    public Object getUsersInfo() {

        return JacksonUtils.toObj("{\n" +
                "    \"id\": \"4291d7da9005377ec9aec4a71ea837f\",\n" +
                "    \"name\": \"datalink\",\n" +
                "    \"username\": \"admin\",\n" +
                "    \"password\": \"\",\n" +
                "    \"avatar\": \"/avatar2.jpg\",\n" +
                "    \"status\": 1,\n" +
                "    \"telephone\": \"\",\n" +
                "    \"lastLoginIp\": \"27.154.74.117\",\n" +
                "    \"lastLoginTime\": 1534837621348,\n" +
                "    \"creatorId\": \"admin\",\n" +
                "    \"createTime\": 1497160610259,\n" +
                "    \"merchantCode\": \"TLif2btpzg079h15bk\",\n" +
                "    \"deleted\": 0,\n" +
                "    \"roleId\": \"admin\",\n" +
                "    \"role\": {\n" +
                "\t\"id\": \"admin\",\n" +
                "\t\"name\": \"管理员\",\n" +
                "\t\"describe\": \"拥有所有权限\",\n" +
                "\t\"status\": 1,\n" +
                "\t\"creatorId\": \"system\",\n" +
                "\t\"createTime\": 1497160610259,\n" +
                "\t\"deleted\": 0,\n" +
                "\t\"permissions\": [{\n" +
                "\t\t\"roleId\": \"admin\",\n" +
                "\t\t\"permissionId\": \"dashboard\",\n" +
                "\t\t\"permissionName\": \"仪表盘\",\n" +
                "\t\t\"actions\": \"[]\",\n" +
                "\t\t\"actionEntitySet\": [],\n" +
                "\t\t\"actionList\": null,\n" +
                "\t\t\"dataAccess\": null\n" +
                "\t}, {\n" +
                "\t\t\"roleId\": \"admin\",\n" +
                "\t\t\"permissionId\": \"exception\",\n" +
                "\t\t\"permissionName\": \"异常页面权限\",\n" +
                "\t\t\"actions\": \"[]\",\n" +
                "\t\t\"actionEntitySet\": [],\n" +
                "\t\t\"actionList\": null,\n" +
                "\t\t\"dataAccess\": null\n" +
                "\t}]\n" +
                "}\n" +
                "  }");
    }

    /**
     * Get paged users.
     *
     * @param pageNo   number index of page
     * @param pageSize size of page
     * @return A collection of users, empty set if no user is found
     */
    @GetMapping
    public Object getUsers(@RequestParam int pageNo, @RequestParam int pageSize) {
        return userDetailsService.getUsersFromDatabase(pageNo, pageSize);
    }

    /**
     * Login to Leon
     *
     * <p>This methods uses username and password to require a new token.
     *
     * @param username username of user
     * @param password password
     * @param response http response
     * @param request  http request
     * @return new token of the user
     * @throws AccessException if user info is incorrect
     */
    @PostMapping("/login")
    public Object login(@RequestParam String username, @RequestParam String password, HttpServletResponse response,
                        HttpServletRequest request) throws AccessException {

        DatalinkUser user = (DatalinkUser) authService.login(request);

        response.addHeader(DatalinkAuthConfig.AUTHORIZATION_HEADER, DatalinkAuthConfig.TOKEN_PREFIX + user.getToken());

        ObjectNode result = JacksonUtils.createEmptyJsonNode();
        result.put(Constants.ACCESS_TOKEN, user.getToken());
        result.put(Constants.TOKEN_TTL, authConfig.getTokenValidityInSeconds());
        result.put(Constants.GLOBAL_ADMIN, user.isGlobalAdmin());
        result.put(Constants.USERNAME, user.getUsername());
        return result;
    }

}
