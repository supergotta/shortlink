package com.supergotta.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.dto.req.RecycleBinDeleteReqDTO;
import com.supergotta.shortlink.project.dto.req.RecycleBinPageReqDTO;
import com.supergotta.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.supergotta.shortlink.project.dto.req.RecycleBinSaveReqDTO;
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
    IPage<ShortLinkPageRespDTO> pageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO);

    /**
     * 恢复短链接
     * @param recycleBinRecoverReqDTO 请求对象
     */
    void recover(RecycleBinRecoverReqDTO recycleBinRecoverReqDTO);

    /**
     * 删除回收站中短链接
     * @param recycleBinDeleteReqDTO 请求对象
     */
    void delete(RecycleBinDeleteReqDTO recycleBinDeleteReqDTO);
}
