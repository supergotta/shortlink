package com.supergotta.shortlink.admin.common.enums;

import com.supergotta.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCodeEnum implements IErrorCode {
    USER_TOKEN_FAIL("A000200", "用户Token验证失败"),
    USER_NULL("B000200", "用户记录不存在"),
    USERNAME_EXISTED("B000201", "用户名已经存在"),
    USER_EXISTED("B000202", "用户记录已经存在"),
    USER_SAVE_ERROR("B000203", "用户信息新增失败");

    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
