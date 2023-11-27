package com.supergotta.shortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登陆返回响应
 */

@Data
@AllArgsConstructor
public class UserLoginRespDTO {

    /**
     * 用户token
     */
    private String token;
}
