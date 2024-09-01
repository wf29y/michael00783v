

package com.leon.datalink.web.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leon.datalink.core.utils.PasswordEncoderUtil;
import com.leon.datalink.web.model.RestResult;
import com.leon.datalink.web.model.RestResultUtils;
import com.leon.datalink.web.model.User;
import com.leon.datalink.web.security.DatalinkUser;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.web.auth.DatalinkUserService;
import com.leon.datalink.web.config.AuthConfig;
import com.leon.datalink.web.exception.AccessException;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.auth.DatalinkUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User related methods entry.
 *
 * @author Leon
 */
@RestController
@RequestMapping("/api/auth/user")
public class UserController {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private DatalinkUserService datalinkUserService;

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
                "    \"username\": \"datalink\",\n" +
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
     * Login to datalink
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

        DatalinkUser user = (DatalinkUser) datalinkUserService.login(request);

        response.addHeader(DatalinkAuthConfig.AUTHORIZATION_HEADER, DatalinkAuthConfig.TOKEN_PREFIX + user.getToken());

        ObjectNode result = JacksonUtils.createEmptyJsonNode();
        result.put(Constants.ACCESS_TOKEN, user.getToken());
        result.put(Constants.TOKEN_TTL, authConfig.getTokenValidityInSeconds());
        result.put(Constants.GLOBAL_ADMIN, user.isGlobalAdmin());
        result.put(Constants.USERNAME, user.getUsername());
        return result;
    }


    /**
     * Update password.
     *
     * @param oldPassword old password
     * @param newPassword new password
     * @return Code 200 if update successfully, Code 401 if old password invalid, otherwise 500
     */
    @PutMapping("/password")
    @Deprecated
    public RestResult<String> updatePassword(@RequestParam(value = "oldPassword") String oldPassword,
                                             @RequestParam(value = "newPassword") String newPassword) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = datalinkUserService.getUserByUsername(username);
        String password = user.getPassword();

        // TODO: throw out more fine grained exceptions
        try {
            if (PasswordEncoderUtil.matches(oldPassword, password)) {
                datalinkUserService.updateUserPassword(username, PasswordEncoderUtil.encode(newPassword));
                return RestResultUtils.success("Update password success");
            }
            return RestResultUtils.failedWithMsg(HttpStatus.UNAUTHORIZED.value(), "Old password is invalid");
        } catch (Exception e) {
            return RestResultUtils.failedWithMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Update userpassword failed");
        }
    }

}
