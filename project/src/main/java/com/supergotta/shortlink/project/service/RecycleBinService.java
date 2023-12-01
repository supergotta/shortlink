package com.supergotta.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService {

    /**
     * 短链接转入回收站
     * @param recycleBinSaveReqDTO 请求对象
     */
    void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO);

    /**
     * 分页查询短链接
     * @param shortLinkPageReqDTO 分页查询短链接请求对象
     * @return 查询结果
     */
    IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkPageReqDTO shortLinkPageReqDTO);
}
