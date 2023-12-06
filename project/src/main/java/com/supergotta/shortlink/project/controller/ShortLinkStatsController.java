package com.supergotta.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.common.convention.result.Result;
import com.supergotta.shortlink.project.common.convention.result.Results;
import com.supergotta.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsAccessLogReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkStatsAccessLogRespDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.supergotta.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    private final ShortLinkStatsService shortLinkStatsService;

    /**
     * 访问单个短链接指定时间内详细监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO){
        return Results.success(shortLinkStatsService.oneShortLinkStats(shortLinkStatsReqDTO));
    }

    /**
     * 访问分组内指定时间内详细监控数据
     */
    @GetMapping("/api/short-link/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO){
        return Results.success(shortLinkStatsService.groupShortLinkStats(shortLinkGroupStatsReqDTO));
    }

    /**
     * 分页查询指定时间内访问日志
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessLogRespDTO>> shortLinkStatsAccessLog(ShortLinkStatsAccessLogReqDTO shortLinkStatsAccessLogDTO) {
        return Results.success(shortLinkStatsService.shortLinkStatsAccessLog(shortLinkStatsAccessLogDTO));
    }
}
