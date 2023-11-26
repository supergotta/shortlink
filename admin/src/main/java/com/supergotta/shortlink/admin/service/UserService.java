package com.supergotta.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supergotta.shortlink.admin.dao.entity.UserDO;
import com.supergotta.shortlink.admin.dto.req.UserRegisterReqDTO;
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
}
