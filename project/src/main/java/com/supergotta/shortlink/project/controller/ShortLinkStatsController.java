package com.supergotta.shortlink.project.controller;

import com.supergotta.shortlink.project.common.convention.result.Result;
import com.supergotta.shortlink.project.common.convention.result.Results;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.supergotta.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    private final ShortLinkStatsService shortLinkStatsService;

    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO){
        return Results.success(shortLinkStatsService.oneShortLinkStats(shortLinkStatsReqDTO));
    }
}
