package com.supergotta.shortlink.project.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.supergotta.shortlink.project.common.constant.ShortLinkConstant;

import java.util.Date;

/**
 * 短链接工具类
 */
public class LinkUtil {

    /**
     * 获取短链接缓存有效期时间
     * @param validDate 有效期日期
     * @return 有效期时间
     */
    public static long getLinkCacheValidDate(Date validDate){
        if (validDate == null){
            // 说明为永久的date
            return ShortLinkConstant.DEFAULT_CACHE_EXPIRATION;
        }
        // 说明为有限期的date
        return DateUtil.between(new Date(), validDate, DateUnit.MS)
    }
}
