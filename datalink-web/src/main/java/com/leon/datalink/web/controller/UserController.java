

package com.leon.datalink.web.controller;


import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.PasswordEncoderUtil;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.config.AuthConfig;
import com.leon.datalink.web.exception.AccessException;
import com.leon.datalink.web.model.RestResult;
import com.leon.datalink.web.model.RestResultUtils;
import com.leon.datalink.web.model.User;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.service.UserService;
import com.leon.datalink.web.util.BaseContextUtil;
import com.leon.datalink.web.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户管理
 *
 * @author Leon
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户详情
     *
     * @param username
     * @return
     */
    @GetMapping("/info")
    public Object getUser(@RequestParam("username") String username) {
        return userService.get(username);
    }

    /**
     * 新增用户
     *
     * @param user
     * @throws Exception
     */
    @PostMapping("/add")
    public void addRule(@RequestBody User user) throws Exception {
        ValidatorUtil.isNotEmpty(user.getUsername(), user.getPassword());
        user.setPassword(PasswordEncoderUtil.encode(user.getPassword()));
        user.setCreateTime(DateUtil.now());
        user.setPermissions(Lists.newArrayList("dashboard"));
        userService.add(user);
    }


    /**
     * 查询用户
     *
     * @param user
     */
    @PostMapping("/list")
    public Object listUser(@RequestBody User user) {
        return userService.list(user);
    }

    /**
     * 移除用户
     *
     * @param user
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeUser(@RequestBody User user) throws Exception {
        ValidatorUtil.isNotEmpty(user.getUsername());
        userService.remove(user.getUsername());
    }

    /**
     * 更新用户
     *
     * @param user
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateUser(@RequestBody User user) throws Exception {
        ValidatorUtil.isNotEmpty(user.getUsername());
        userService.update(user);
    }


    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @GetMapping("/password")
    public RestResult<String> updatePassword(@RequestParam(value = "username") String username,
                                             @RequestParam(value = "oldPassword") String oldPassword,
                                             @RequestParam(value = "newPassword") String newPassword) {
        try {
            User user = userService.get(username);
            if (PasswordEncoderUtil.matches(oldPassword, user.getPassword())) {
                userService.updateUserPassword(username, PasswordEncoderUtil.encode(newPassword));
                return RestResultUtils.success("Update password success");
            }
            return RestResultUtils.failedWithMsg(HttpStatus.UNAUTHORIZED.value(), "Old password is invalid");
        } catch (Exception e) {
            return RestResultUtils.failedWithMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Update userpassword failed");
        }
    }


}
