package com.supergotta.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supergotta.shortlink.admin.common.biz.user.UserContext;
import com.supergotta.shortlink.admin.common.convention.result.Result;
import com.supergotta.shortlink.admin.common.exception.ServiceException;
import com.supergotta.shortlink.admin.dao.entity.GroupDO;
import com.supergotta.shortlink.admin.dao.mapper.GroupMapper;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.supergotta.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.supergotta.shortlink.admin.remote.ShortLinkRemoteService;
import com.supergotta.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
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

    /**
     * 后续改为openFeign调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

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
                .username(UserContext.getUsername())
                .name(groupName)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupReqDTO> listGroup() {
        String username = UserContext.getUsername();

        List<GroupDO> groupList = lambdaQuery()
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, username)
                .orderByDesc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getUpdateTime)
                .list();
        String response = shortLinkRemoteService.listGroupShortLinkCount(groupList.stream().map(GroupDO::getGid).toList());
        TypeReference<Result<List<ShortLinkGroupCountQueryRespDTO>>> typeReference = new TypeReference<>() {};
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = JSON.parseObject(response, typeReference);
        List<ShortLinkGroupCountQueryRespDTO> dataList = listResult.getData();

        List<ShortLinkGroupReqDTO> shortLinkGroupReqDTOS = BeanUtil.copyToList(groupList, ShortLinkGroupReqDTO.class);
        for (ShortLinkGroupReqDTO shortLinkGroupReqDTO : shortLinkGroupReqDTOS) {
            for (ShortLinkGroupCountQueryRespDTO shortLinkGroupCountQueryRespDTO : dataList) {
                if (shortLinkGroupReqDTO.getGid().equals(shortLinkGroupCountQueryRespDTO.getGid())){
                    shortLinkGroupReqDTO.setShortLinkCount(shortLinkGroupCountQueryRespDTO.getShortLinkCount());
                }
            }
        }

        return shortLinkGroupReqDTOS;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO) {
        String username = UserContext.getUsername();
        boolean updateSuccess = lambdaUpdate()
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, shortLinkGroupUpdateReqDTO.getGid())
                .eq(GroupDO::getDelFlag, 0)
                .update(GroupDO.builder().name(shortLinkGroupUpdateReqDTO.getName()).build());
        if (!updateSuccess){
            throw new ServiceException("更新失败");
        }
    }

    @Override
    public void deleteGroup(String gid) {
        // 采用软删除方式, 仅修改delFlag为1
        boolean updateSuccess = lambdaUpdate()
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0)
                .update(GroupDO.builder().delFlag(1).build());
        if (!updateSuccess){
            throw new ServiceException("删除失败");
        }
    }

    @Override
    public void updateGroupSort(List<ShortLinkGroupSortReqDTO> shortLinkGroupSortReqDTOS) {
        shortLinkGroupSortReqDTOS.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            boolean updateSuccess = lambdaUpdate()
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0)
                    .update(groupDO);
            if (!updateSuccess){
                throw new ServiceException("更新失败");
            }
        });
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
