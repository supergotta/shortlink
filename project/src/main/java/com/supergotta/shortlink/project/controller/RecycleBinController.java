package com.supergotta.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.common.convention.result.Result;
import com.supergotta.shortlink.project.common.convention.result.Results;
import com.supergotta.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.supergotta.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO){
        recycleBinService.saveRecycleBin(recycleBinSaveReqDTO);
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(
            @RequestParam("gid") String gid,
            @RequestParam("current") String current,
            @RequestParam("size") String size){
        ShortLinkPageReqDTO shortLinkPageReqDTO = new ShortLinkPageReqDTO();
        shortLinkPageReqDTO.setGid(gid);
        shortLinkPageReqDTO.setCountId(current);
        shortLinkPageReqDTO.setSize(Long.parseLong(size));
        return Results.success(recycleBinService.pageRecycleBin(shortLinkPageReqDTO));
    }
}
