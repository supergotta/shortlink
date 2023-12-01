package com.supergotta.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.common.constant.RedisKeyConstant;
import com.supergotta.shortlink.project.dao.entity.ShortLinkDO;
import com.supergotta.shortlink.project.dto.req.RecycleBinPageReqDTO;
import com.supergotta.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.supergotta.shortlink.project.service.RecycleBinService;
import com.supergotta.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final ShortLinkService shortLinkService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        // 通过将link表中的enable_statue字段设为1标识该链接不可用, 进而表示移至回收站中
        boolean isSuccess = shortLinkService.lambdaUpdate()
                .eq(ShortLinkDO::getFullShortUrl, recycleBinSaveReqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, recycleBinSaveReqDTO.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .set(ShortLinkDO::getEnableStatus, 1)
                .update();
        log.info("更新状态为:{}", isSuccess);

        // 移到回收站的短链接应该同时删除对应的缓存
        stringRedisTemplate.delete(RedisKeyConstant.GOTO_SHORT_LINK_KRY + recycleBinSaveReqDTO.getFullShortUrl());
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO) {

        IPage<ShortLinkDO> page = shortLinkService.lambdaQuery()
                .in(ShortLinkDO::getGid, recycleBinPageReqDTO.getGid())
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelFlag, 0)
                .page(recycleBinPageReqDTO);

        return page.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }
}
