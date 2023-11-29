package com.supergotta.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 短链接创建响应对象
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ShortLinkCreateRespDTO {

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

}
