package com.supergotta.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.common.convention.result.Result;
import com.supergotta.shortlink.project.common.convention.result.Results;
import com.supergotta.shortlink.project.dto.req.RecycleBinPageReqDTO;
import com.supergotta.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
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

    /**
     * 保存回收站
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO){
        recycleBinService.saveRecycleBin(recycleBinSaveReqDTO);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
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

    /**
     * 恢复回收站中的短链接
     */
    @PutMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recover(@RequestBody RecycleBinRecoverReqDTO recycleBinRecoverReqDTO){
        recycleBinService.recover(recycleBinRecoverReqDTO);
        return Results.success();
    }
}
