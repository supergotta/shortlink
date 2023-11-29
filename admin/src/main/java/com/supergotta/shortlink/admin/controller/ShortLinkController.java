package com.supergotta.shortlink.admin.controller;

import com.supergotta.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.supergotta.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.springframework.web.bind.annotation.*;

/**
 * 短链接后管控制层
 */
@RestController
public class ShortLinkController {

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
}
