package com.supergotta.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supergotta.shortlink.admin.dao.entity.UserDO;
import com.supergotta.shortlink.admin.dto.req.UserLoginReqDTO;
import com.supergotta.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.supergotta.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.supergotta.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.supergotta.shortlink.admin.dto.resp.UserRespDTO;


public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 返回响应实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否存在
     * @param username 用户名
     * @return 用户名存在返回 True, 不存在返回 False
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam 注册用户请求参数
     */
    void Register(UserRegisterReqDTO requestParam);

    /**
     * 根据用户名修改用户
     * @param userUpdateReqDTO 修改用户请求参数
     */
    void updateByUsername(UserUpdateReqDTO userUpdateReqDTO);

    /**
     * 用户登陆
     * @param userLoginReqDTO 用户登陆请求参数
     */
    UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO);

    /**
     * 检查是否登陆
     * @param token 请求中传入的token
     * @return 用户是否登陆
     */
    Boolean checkLogin(String token, String username);
}
