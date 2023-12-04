package com.supergotta.shortlink.project.service;

import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

public interface ShortLinkStatsService {
    /**
     * 获取单个短链接监控数据
     * @param shortLinkStatsReqDTO 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO);
}
