package com.supergotta.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.common.convention.result.Result;
import com.supergotta.shortlink.project.common.convention.result.Results;
import com.supergotta.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.supergotta.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 短链接控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO shortLinkCreateReqDTO){
        return Results.success(shortLinkService.createShortLink(shortLinkCreateReqDTO));
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(
            @RequestParam("gid") String gid,
            @RequestParam("current") String current,
            @RequestParam("size") String size){
        ShortLinkPageReqDTO shortLinkPageReqDTO = new ShortLinkPageReqDTO();
        shortLinkPageReqDTO.setGid(gid);
        shortLinkPageReqDTO.setCountId(current);
        shortLinkPageReqDTO.setSize(Long.parseLong(size));
        return Results.success(shortLinkService.pageShortLink(shortLinkPageReqDTO));
    }

}
