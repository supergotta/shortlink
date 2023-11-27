package com.supergotta.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supergotta.shortlink.admin.dao.entity.GroupDO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;

/**
 * 短链接分组服务层接口
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     * @param shortLinkGroupSaveReqDTO 新增分组请求响应对象
     */
    void saveGroup(ShortLinkGroupSaveReqDTO shortLinkGroupSaveReqDTO);

}