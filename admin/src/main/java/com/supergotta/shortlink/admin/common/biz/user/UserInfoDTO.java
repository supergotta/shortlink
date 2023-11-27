package com.supergotta.shortlink.admin.common.biz.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 用户信息实体
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserInfoDTO {
    /**
     * 用户 ID
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;
}
