package com.supergotta.shortlink.project.common.constant;

public class RedisKeyConstant {
    /**
     * 短链接跳转前缀Key
     */
    public static final String GOTO_SHORT_LINK_KRY = "short-link_goto_";

    /**
     * 防止缓存穿透的锁前缀
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "short-link_lock_goto_";

    public static final String GOTO_IS_NULL_SHORT_LINK_KEY = "short-link_goto_is_null_";
}
