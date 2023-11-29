package com.supergotta.shortlink.project.dao.mapper.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supergotta.shortlink.project.common.exception.ServiceException;
import com.supergotta.shortlink.project.dao.entity.ShortLinkDO;
import com.supergotta.shortlink.project.dao.mapper.ShortLinkMapper;
import com.supergotta.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.supergotta.shortlink.project.service.ShortLinkService;
import com.supergotta.shortlink.project.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;


/**
 * 短链接服务层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO) {
        //通过工具类获取短链接的前缀
        String originUrl = shortLinkCreateReqDTO.getOriginUrl();
        String shortLinkSuffix = HashUtil.hashToBase62(originUrl);

        //保证生成短链接前缀的唯一性
        int generateCount = 0;
        while (true) {
            if (generateCount > 10) {
                throw new ServiceException("短链接频繁生成, 请稍后再尝试");
            }
            if (!shortUriCreateCachePenetrationBloomFilter.contains(shortLinkSuffix)){
                //该短链接还没有创建过
                break;
            }
            //该短链接已经创建过了, 那么需要重新创建一个短链接
            originUrl += System.currentTimeMillis();
            shortLinkSuffix = HashUtil.hashToBase62(originUrl);
            generateCount++;
        }

        //将DTO中的信息赋值到DO中
        ShortLinkDO shortLinkDO = BeanUtil.copyProperties(shortLinkCreateReqDTO, ShortLinkDO.class);
        //获取整体短链接
        shortLinkDO.setFullShortUrl(shortLinkCreateReqDTO.getDomain() + "/" + shortLinkSuffix);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setEnableStatus(0);
        //将得到的DO对象存入数据库中
        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException e) {
            //当布隆过滤器误判时, 会导致短链接重复入库, 这时候上面的insert语句会抛出这个异常
            ShortLinkDO one = lambdaQuery().eq(ShortLinkDO::getShortUri, shortLinkSuffix).one();
            if (one != null){
                log.warn("短链接:{} 重复入库", shortLinkDO.getFullShortUrl());
                throw new ServiceException("短链接生成重复");
            }
        }
        //将刚刚生成的短链接添加到布隆过滤器
        shortUriCreateCachePenetrationBloomFilter.add(shortLinkSuffix);
        //返回响应信息
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(shortLinkCreateReqDTO.getOriginUrl())
                .gid(shortLinkCreateReqDTO.getGid())
                .build();
    }
}
