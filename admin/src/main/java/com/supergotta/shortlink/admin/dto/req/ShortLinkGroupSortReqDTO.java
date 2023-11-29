package com.supergotta.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组修改排序请求实体
 */
@Data
public class ShortLinkGroupSortReqDTO {

    /**
     * 分组ID
     */
    private String gid;

    /**
     * 位置
     */
    private Integer sortOrder;
}
