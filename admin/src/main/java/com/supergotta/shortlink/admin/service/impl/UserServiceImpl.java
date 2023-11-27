package com.supergotta.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supergotta.shortlink.admin.common.constant.RedisCacheConstant;
import com.supergotta.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.supergotta.shortlink.admin.common.exception.ClientException;
import com.supergotta.shortlink.admin.dao.entity.UserDO;
import com.supergotta.shortlink.admin.dao.mapper.UserMapper;
import com.supergotta.shortlink.admin.dto.req.UserLoginReqDTO;
import com.supergotta.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.supergotta.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.supergotta.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.supergotta.shortlink.admin.dto.resp.UserRespDTO;
import com.supergotta.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        // 生成查询条件
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);

        // 判断是否为空
        if (userDO == null) {
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
        if (!hasUsername(requestParam.getUsername())) {
            throw new ClientException(UserErrorCodeEnum.USERNAME_EXISTED);
        }

        // 获取Redisson分布式锁
        String key = RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername();
        RLock lock = redissonClient.getLock(key);

        // 尝试获取锁
        try {
            if (lock.tryLock()) {
                // 新增用户
                int insertCount = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if (insertCount < 1) {
                    throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                }
                // 将username插入到布隆过滤器中
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            } else {
                throw new ClientException(UserErrorCodeEnum.USERNAME_EXISTED);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateByUsername(UserUpdateReqDTO userUpdateReqDTO) {
        //TODO 验证当前用户名是否为登陆用户

        // 生成更新查询条件
        LambdaUpdateWrapper<UserDO> wrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, userUpdateReqDTO.getUsername());
        // 更新用户
        baseMapper.update(BeanUtil.toBean(userUpdateReqDTO, UserDO.class), wrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        // 1.查询用户名与密码是否存在数据库中
        // 1.1生成查询条件
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, userLoginReqDTO.getUsername())
                .eq(UserDO::getPassword, userLoginReqDTO.getPassword())
                .eq(UserDO::getDelFlag, 0);
        // 1.2根据条件进行查询
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        // 2.如果返回结果为空则说明登陆失败
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }
        // 3.登陆操作
        String hashKey = "login_" + userLoginReqDTO.getUsername();
        //3.0 查询当前用户是否已经登陆过了
        Boolean isLogin = stringRedisTemplate.hasKey(hashKey);
        if (isLogin != null && isLogin){
            throw new ClientException("用户已经登陆");
        }
        // 3.1 生成UUID作为token
        String key = UUID.randomUUID().toString();
        // 3.2 将token, 以及token对应的用户对象存入到redis中, 并设置30分中的有效期
        stringRedisTemplate.opsForHash().put(hashKey, key, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(hashKey, 30, TimeUnit.DAYS);
        // 3.3 将token返回给前端
        return new UserLoginRespDTO(key);
    }

    @Override
    public Boolean checkLogin(String token, String username) {
        // 检查token是否存在key中
        String hashKey = "login_" + username;
        return stringRedisTemplate.opsForHash().get(hashKey, token) != null;
    }

    @Override
    public void logout(String token, String username) {
        String hashKey = "login_" + username;
        if (checkLogin(token, username)){
            stringRedisTemplate.delete(hashKey);
            return;
        }
        throw new ClientException("用户未登录");
    }
}
