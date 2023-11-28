package com.supergotta.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supergotta.shortlink.admin.dao.entity.GroupDO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;

import java.util.List;

/**
 * 短链接分组服务层接口
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     * @param shortLinkGroupSaveReqDTO 新增分组请求响应对象
     */
    void saveGroup(ShortLinkGroupSaveReqDTO shortLinkGroupSaveReqDTO);

    /**
     * 查询用户短链接分组集合
     * @return 当前用户对应的集合
     */
    List<ShortLinkGroupReqDTO> listGroup();

    /**
     * 修改短链接分组名
     * @param shortLinkGroupUpdateReqDTO 短链接分组修改请求实体
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO);
}
