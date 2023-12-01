package com.supergotta.shortlink.project.dto.req;

import lombok.Data;

/**
 * 回收站保存请求
 */
@Data
public class RecycleBinSaveReqDTO {
    /**
     * 分组标识
     */
    String gid;
    /**
     * 整个短链接
     */
    String fullShortUrl;

}
