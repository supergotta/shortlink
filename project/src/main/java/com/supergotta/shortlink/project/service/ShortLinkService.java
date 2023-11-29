package com.supergotta.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supergotta.shortlink.project.dao.entity.ShortLinkDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkPageRespDTO;

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

    /**
     * 分页查询短链接
     * @param shortLinkPageReqDTO 分页查询短链接请求对象
     * @return 查询结果
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO);
}
