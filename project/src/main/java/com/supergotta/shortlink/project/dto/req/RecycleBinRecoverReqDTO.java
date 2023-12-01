package com.supergotta.shortlink.project.dto.req;

import lombok.Data;

@Data
public class RecycleBinRecoverReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 整个短链接
     */
    private String fullShortUrl;
}
