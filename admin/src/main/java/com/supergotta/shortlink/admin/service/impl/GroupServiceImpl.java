package com.supergotta.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supergotta.shortlink.admin.dao.entity.GroupDO;
import com.supergotta.shortlink.admin.dao.mapper.GroupMapper;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.supergotta.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * 短链接服务层实现类
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(ShortLinkGroupSaveReqDTO shortLinkGroupSaveReqDTO) {

        String groupName = shortLinkGroupSaveReqDTO.getName();
        //1. 为gid字段生成随机数, 如果有重复的话就再生成
        String gid = generateRandomString();
        GroupDO group = lambdaQuery().eq(GroupDO::getGid, gid).one();
        while (group != null){
            gid = generateRandomString();
            group = lambdaQuery().eq(GroupDO::getGid, gid).one();
        }
        //2. 拿着上面创建好的gid创建Group对象并插入
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .sortOrder(0)
                // TODO 设置用户名
                .name(groupName)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupReqDTO> listGroup() {
        // TODO 获取用户名
        String username = "abc";

        List<GroupDO> groupList = lambdaQuery()
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, username)
                .orderByDesc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getUpdateTime)
                .list();
        return BeanUtil.copyToList(groupList, ShortLinkGroupReqDTO.class);
    }

    // 生成一个包含数字和字母的6位随机字符串的方法
    public static String generateRandomString() {
        String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(alphaNumeric.length());
            sb.append(alphaNumeric.charAt(index));
        }

        return sb.toString();
    }
}
