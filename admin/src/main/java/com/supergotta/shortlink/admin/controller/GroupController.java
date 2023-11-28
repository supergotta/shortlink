package com.supergotta.shortlink.admin.controller;

import com.supergotta.shortlink.admin.common.convention.result.Result;
import com.supergotta.shortlink.admin.common.convention.result.Results;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.supergotta.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接分组控制层
 */
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增短链接分组
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> sava(@RequestBody ShortLinkGroupSaveReqDTO shortLinkGroupSaveReqDTO){
        groupService.saveGroup(shortLinkGroupSaveReqDTO);
        return Results.success();
    }

    /**
     * 查询用户短链接分组集合
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupReqDTO>> listGroup(){
        return Results.success(groupService.listGroup());
    }

    /**
     * 修改短链接分组名
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO){
        groupService.updateGroup(shortLinkGroupUpdateReqDTO);
        return Results.success();
    }

    /**
     * 删除短链接分组
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestParam String gid){
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 修改短链接排序
     */
    @PutMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> updateGroupSort(@RequestBody List<ShortLinkGroupSortReqDTO> shortLinkGroupSortReqDTOS){
        groupService.updateGroupSort(shortLinkGroupSortReqDTOS);
        return Results.success();
    }

}
