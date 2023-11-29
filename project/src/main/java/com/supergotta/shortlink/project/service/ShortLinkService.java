package com.supergotta.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supergotta.shortlink.project.dao.entity.ShortLinkDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * 短链接服务层接口
 */
public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     * @param shortLinkCreateReqDTO 创建短链接请求对象
     * @return 创建短链接响应对象
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO);
}
