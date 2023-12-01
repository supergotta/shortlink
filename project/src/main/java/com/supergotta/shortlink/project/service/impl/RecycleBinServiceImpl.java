package com.supergotta.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.supergotta.shortlink.project.common.constant.RedisKeyConstant;
import com.supergotta.shortlink.project.dao.entity.ShortLinkDO;
import com.supergotta.shortlink.project.dto.req.RecycleBinDeleteReqDTO;
import com.supergotta.shortlink.project.dto.req.RecycleBinPageReqDTO;
import com.supergotta.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
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

    @Override
    public void recover(RecycleBinRecoverReqDTO recycleBinRecoverReqDTO) {
        //1. 将数据库中对应的短链接的enable_state恢复为0
        boolean isSuccess = shortLinkService.lambdaUpdate()
                .eq(ShortLinkDO::getFullShortUrl, recycleBinRecoverReqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, recycleBinRecoverReqDTO.getGid())
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelFlag, 0)
                .set(ShortLinkDO::getEnableStatus, 0)
                .update();
        log.info("更新状态为:{}", isSuccess);

        //2. 将redis中, 该短链接对应的空值删除
        stringRedisTemplate.delete(RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY + recycleBinRecoverReqDTO.getFullShortUrl());

    }

    @Override
    public void delete(RecycleBinDeleteReqDTO recycleBinDeleteReqDTO) {
        // 删除数据库中数据
        // TODO 这里用的是remove函数, 注意直接删掉了, 至于老师用的delete函数有没有简单地将del_flag赋值为1, 需要看看
        LambdaQueryWrapper<ShortLinkDO> removeWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, recycleBinDeleteReqDTO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, recycleBinDeleteReqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelFlag, 0);
        boolean isSuccess = shortLinkService.remove(removeWrapper);
        log.info("删除状态为:{}", isSuccess);
    }
}
