package com.supergotta.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsAccessLogReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkStatsAccessLogRespDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

public interface ShortLinkStatsService {
    /**
     * 获取单个短链接监控数据
     * @param shortLinkStatsReqDTO 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 分页查询访问日志表
     * @param shortLinkStatsAccessLogDTO 请求对象
     * @return 查询结果
     */
    IPage<ShortLinkStatsAccessLogRespDTO> shortLinkStatsAccessLog(ShortLinkStatsAccessLogReqDTO shortLinkStatsAccessLogDTO);

    /**
     * 获取分组在指定日期范围内监控数据
     * @param shortLinkGroupStatsReqDTO 请求对象
     * @return 分组的监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
