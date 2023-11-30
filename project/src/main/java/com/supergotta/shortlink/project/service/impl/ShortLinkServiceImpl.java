package com.supergotta.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supergotta.shortlink.project.common.constant.RedisKeyConstant;
import com.supergotta.shortlink.project.common.enums.ValidDateType;
import com.supergotta.shortlink.project.common.exception.ServiceException;
import com.supergotta.shortlink.project.dao.entity.ShortLinkDO;
import com.supergotta.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.supergotta.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.supergotta.shortlink.project.dao.mapper.ShortLinkMapper;
import com.supergotta.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.supergotta.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.supergotta.shortlink.project.service.ShortLinkService;
import com.supergotta.shortlink.project.util.HashUtil;
import com.supergotta.shortlink.project.util.LinkUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 短链接服务层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

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
            if (!shortUriCreateCachePenetrationBloomFilter.contains(shortLinkSuffix)) {
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
        // 新建短链接的同时将短链接FullShortUrl和分组标识gid的关系存入Goto表中
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder().FullShortLink(shortLinkDO.getFullShortUrl()).gid(shortLinkDO.getGid()).build();
        //将得到的DO对象存入数据库中
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException e) {
            //当布隆过滤器误判时, 会导致短链接重复入库, 这时候上面的insert语句会抛出这个异常
            ShortLinkDO one = lambdaQuery().eq(ShortLinkDO::getShortUri, shortLinkSuffix).one();
            if (one != null) {
                log.warn("短链接:{} 重复入库", shortLinkDO.getFullShortUrl());
                throw new ServiceException("短链接生成重复");
            }
        }
        // 缓存预热
        stringRedisTemplate.opsForValue().set(shortLinkDO.getFullShortUrl(),
                originUrl,
                LinkUtil.getLinkCacheValidDate(shortLinkCreateReqDTO.getValidDate()),
                TimeUnit.MILLISECONDS
        );
        //将刚刚生成的短链接添加到布隆过滤器
        shortUriCreateCachePenetrationBloomFilter.add(shortLinkDO.getFullShortUrl());
        //返回响应信息
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(shortLinkCreateReqDTO.getOriginUrl())
                .gid(shortLinkCreateReqDTO.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        IPage<ShortLinkDO> page = lambdaQuery()
                .eq(ShortLinkDO::getGid, shortLinkPageReqDTO.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .page(shortLinkPageReqDTO);

        return page.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gids) {
        QueryWrapper<ShortLinkDO> wrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid", "count(*) as shortLinkCount")
                .in("gid", gids)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkList = baseMapper.selectMaps(wrapper);
        return BeanUtil.copyToList(shortLinkList, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO) {
        ShortLinkDO shortLinkDO = BeanUtil.copyProperties(shortLinkUpdateReqDTO, ShortLinkDO.class);
        // 判断有效期类型, 如果是永久有效的, 那么需要额外将被更新数据的valid_date设为null
        if (shortLinkUpdateReqDTO.getValidDateType() == ValidDateType.PERMANENT.getType()) {
            boolean isSuccess = lambdaUpdate()
                    .eq(ShortLinkDO::getGid, shortLinkUpdateReqDTO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, shortLinkUpdateReqDTO.getFullShortUrl())
                    .set(ShortLinkDO::getValidDate, null)
                    .update(shortLinkDO);
        } else {
            boolean isSuccess = lambdaUpdate()
                    .eq(ShortLinkDO::getGid, shortLinkUpdateReqDTO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, shortLinkUpdateReqDTO.getFullShortUrl())
                    .update(shortLinkDO);
        }

    }

    @Override
    public void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        // 由于t_link是根据gid进行分片, 所以在对当前短链接进行查询钱, 先获得短链接对应的gid
        //1. 通过t_link_goto表获取gid
        //1.1 获取完成的shortUrl
        String fullShortUrl = request.getServerName() + "/" + shortUri;

        // _1.1 解决缓存穿透问题, 首先查看redis缓存中是否有这个链接
        String originalUrl = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORT_LINK_KRY + fullShortUrl);
        if (StrUtil.isNotBlank(originalUrl)) {
            // _1.2 如果查到了这个链接
            try {
                response.sendRedirect(originalUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        // _2.1 如果没有查到这个链接, 说明redis中这个链接失效了, 这里要将链接重新加入缓存, 然后注意使用分布式锁防止大量请求涌入数据库
        // _2.2 首先查询布隆过滤器中是否存在这个fullShortLink
        if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)){
            // _2.3 如果布隆过滤器判断没有这个fullShortLink, 则直接返回(但是这个布隆过滤器也有误判的情况, 比如说不存在的误判成存在了, 那么下面就是解决这个问题的)
            // 直接返回
            return;
        }

        // _3.1 如果布隆过滤器告诉你有这个链接的话, 那么我们首先排查是否是布隆过滤器误判了
        // _3.1 如果误判了, 也就是说数据库确实没有这个链接, 我们采用存储空值到Redis的方式进行规避后续的恶意查询攻击
        // _3.1.1 先判断在Redis中是否存在这个链接对应的空值
        String isNull = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY + fullShortUrl);
        if (StrUtil.isNotBlank(isNull)){
            // _3.1.2 如果有这个链接对应的空值, 说明数据库没有这个链接, 则直接返回
            return;
        }

        // _3.2 如果布隆过滤器表示存在, 同时空值列表里没有这个链接, 说明第一次遇到这个链接的请求
        // _3.2.1我们使用分布式锁查询数据库
        RLock redissonLock = redissonClient.getLock(RedisKeyConstant.LOCK_GOTO_SHORT_LINK_KEY + fullShortUrl);
        redissonLock.tryLock();
        try {
            // 双重检验
            originalUrl = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORT_LINK_KRY + fullShortUrl);
            if (StrUtil.isNotBlank(originalUrl)) {
                // 如果查到了这个链接
                try {
                    response.sendRedirect(originalUrl);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            // 如果还是没有拿到这个链接, 拿到锁的进程去查数据库并存到Redis中
            //1.2 创建t_link_goto表的查询条件
            LambdaQueryWrapper<ShortLinkGotoDO> wrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortLink, fullShortUrl);
            //1.3 查询t_link_goto表
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(wrapper);
            if (shortLinkGotoDO == null) {
                //此处需要进行封控
                // _3.2.2 说明数据库就没有这条记录, 我们按照前面的约定, 在redis中对应存储空值, 这个空值设定为30s
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY + fullShortUrl, "-", 30, TimeUnit.SECONDS);
                return;
            }

            //2. 拿到gid后, 用gid查询短链接对应的原始链接
            ShortLinkDO shortLinkDO = lambdaQuery()
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .one();
            if (shortLinkDO == null) {
                //需要进行封控
                return;
            }
            // 正常情况我们将响应重定向到刚刚的查询结果
            try {
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.GOTO_SHORT_LINK_KRY + fullShortUrl, shortLinkDO.getOriginUrl());
                response.sendRedirect(shortLinkDO.getOriginUrl());
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }finally {
            redissonLock.unlock();
        }
    }
}
