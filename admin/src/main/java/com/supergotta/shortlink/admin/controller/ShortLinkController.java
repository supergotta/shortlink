package com.supergotta.shortlink.admin.controller;

import com.supergotta.shortlink.admin.common.convention.result.Result;
import com.supergotta.shortlink.admin.common.convention.result.Results;
import com.supergotta.shortlink.admin.remote.ShortLinkRemoteService;
import com.supergotta.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.supergotta.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import org.springframework.web.bind.annotation.*;

/**
 * 短链接后管控制层
 */
@RestController
public class ShortLinkController {

    /**
     * 后续改为openFeign调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @PostMapping("/api/short-link/admin/v1/create")
    public String createShortLink(@RequestBody ShortLinkCreateReqDTO shortLinkCreateReqDTO){
        String shortLink = shortLinkRemoteService.createShortLink(shortLinkCreateReqDTO);
        return shortLink;
    }

    @GetMapping("/api/short-link/admin/v1/page")
    public String pageShortLink(
            @RequestParam("gid") String gid,
            @RequestParam("current") String current,
            @RequestParam("size") String size){
        return shortLinkRemoteService.pageShortLink(gid, current, size);
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO shortLinkUpdateReqDTO){
        shortLinkRemoteService.updateShortLink(shortLinkUpdateReqDTO);
        return Results.success();
    }
}
