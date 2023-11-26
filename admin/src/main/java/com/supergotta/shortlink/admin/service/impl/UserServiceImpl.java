package com.supergotta.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supergotta.shortlink.admin.common.constant.RedisCacheConstant;
import com.supergotta.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.supergotta.shortlink.admin.common.exception.ClientException;
import com.supergotta.shortlink.admin.dao.entity.UserDO;
import com.supergotta.shortlink.admin.dao.mapper.UserMapper;
import com.supergotta.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.supergotta.shortlink.admin.dto.resp.UserRespDTO;
import com.supergotta.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        // 生成查询条件
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);

        // 判断是否为空
        if (userDO == null){
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }

        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username) {
        /*LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        return userDO == null;*/
        // 使用布隆过滤器判断username是否存在
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void Register(UserRegisterReqDTO requestParam) {
        // 通过布隆过滤器中是否存在当前username
        if (!hasUsername(requestParam.getUsername())){
            throw new ClientException(UserErrorCodeEnum.USERNAME_EXISTED);
        }

        // 获取Redisson分布式锁
        String key = RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername();
        RLock lock = redissonClient.getLock(key);

        // 尝试获取锁
        try {
            if (lock.tryLock()){
                // 新增用户
                int insertCount = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if (insertCount < 1){
                    throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                }
                // 将username插入到布隆过滤器中
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            }
            throw new ClientException(UserErrorCodeEnum.USERNAME_EXISTED);
        }finally {
            lock.unlock();
        }
    }
}
