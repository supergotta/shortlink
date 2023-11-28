package com.supergotta.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 分组更新请求实体
 */
@Data
public class ShortLinkGroupUpdateReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名
     */
    private String name;
}
