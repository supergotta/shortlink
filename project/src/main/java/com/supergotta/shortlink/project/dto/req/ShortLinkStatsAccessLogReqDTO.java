package com.supergotta.shortlink.project.dto.req;

import lombok.Data;

/**
 * 短链接监控访问日志请求参数
 */
@Data
public class ShortLinkStatsAccessLogReqDTO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 当前页面
     */
    private Integer current;

    /**
     * 每页数据数量
     */
    private Integer size;
}
