package com.supergotta.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.common.convention.result.Result;
import com.supergotta.shortlink.project.common.convention.result.Results;
import com.supergotta.shortlink.project.dto.req.RecycleBinPageReqDTO;
import com.supergotta.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.supergotta.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam("gid") List<String> gid,
            @RequestParam("current") String current,
            @RequestParam("size") String size){
        // TODO 将来在远程调用时gid为用户对应的所有分组
        RecycleBinPageReqDTO recycleBinPageReqDTO = new RecycleBinPageReqDTO();
        recycleBinPageReqDTO.setGid(gid);
        recycleBinPageReqDTO.setCountId(current);
        recycleBinPageReqDTO.setSize(Long.parseLong(size));
        return Results.success(recycleBinService.pageRecycleBin(recycleBinPageReqDTO));
    }
}
