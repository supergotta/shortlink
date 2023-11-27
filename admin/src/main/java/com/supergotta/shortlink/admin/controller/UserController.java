package com.supergotta.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.supergotta.shortlink.admin.common.convention.result.Result;
import com.supergotta.shortlink.admin.common.convention.result.Results;
import com.supergotta.shortlink.admin.dto.req.UserLoginReqDTO;
import com.supergotta.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.supergotta.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.supergotta.shortlink.admin.dto.resp.ActualUserRespDTO;
import com.supergotta.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.supergotta.shortlink.admin.dto.resp.UserRespDTO;
import com.supergotta.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        UserRespDTO userRespDTO = userService.getUserByUsername(username);
        return Results.success(userRespDTO);
    }

    /**
     * 根据用户名查询未脱敏的用户信息
     */
    @GetMapping("/api/short-link/v1/actual/user/{username}")
    public Result<ActualUserRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), ActualUserRespDTO.class));
    }

    /**
     * 查询用户名是否存在
     */
    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam String username){
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 新增用户
     */
    @PostMapping("/api/short-link/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO userRegisterReqDTO){
        userService.Register(userRegisterReqDTO);
        return Results.success();
    }

    /**
     * 修改用户
     */
    @PutMapping("/api/short-link/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO userUpdateReqDTO){
        userService.updateByUsername(userUpdateReqDTO);
        return Results.success();
    }

    /**
     * 用户登陆
     */
    @PostMapping("/api/short-link/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO userLoginReqDTO){
        return Results.success(userService.login(userLoginReqDTO));
    }

    /**
     * 检查用户是否登陆
     */
    @GetMapping("/api/short-link/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("token") String token, @RequestParam("username") String username){
        return Results.success(userService.checkLogin(token, username));
    }
}
