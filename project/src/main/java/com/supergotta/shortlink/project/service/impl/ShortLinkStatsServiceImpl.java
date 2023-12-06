package com.supergotta.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supergotta.shortlink.project.dao.entity.*;
import com.supergotta.shortlink.project.dao.mapper.*;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsAccessLogReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.supergotta.shortlink.project.dto.resp.*;
import com.supergotta.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShortLinkStatsServiceImpl implements ShortLinkStatsService {

    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocalStatsMapper linkLocalStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO) {
        // 解析请求DTO
        String fullShortUrl = shortLinkStatsReqDTO.getFullShortUrl();;
        String gid = shortLinkStatsReqDTO.getGid();
        Date startDate = DateUtil.parse(shortLinkStatsReqDTO.getStartDate());
        Date endDate = DateUtil.parse(shortLinkStatsReqDTO.getEndDate());
        ShortLinkStatsRespDTO result = new ShortLinkStatsRespDTO();

        // *****解析基础访问数据*****
        // 查询基础数据对应的数据库LinkAccessStats, 按日期分组
        List<LinkAccessStatsDO> listStatsByShortLink = linkAccessStatsMapper.listStatsByShortLink(shortLinkStatsReqDTO);
        if (listStatsByShortLink.isEmpty()){
            // 如果没有基础数据的话可以直接返回了
            return null;
        }
        List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        // 1. 获取给定义日期的日期字符串数组
        List<String> dateTimes = DateUtil.rangeToList(startDate, endDate, DateField.DAY_OF_MONTH).stream().map(DateUtil::formatDate).toList();
        // 2. 创建map存储每个日期是否有数据
        Map<String, Boolean> dateIsNotNull = new LinkedHashMap<>();
        for (String dateTime : dateTimes) {
            // 2.1 每个日期初始化为没有数据
            dateIsNotNull.put(dateTime, false);
        }
        // 3. 将listStatsByShortLink转换为基础访问数据 ShortLinkStatsAccessDailyRespDTO
        for (LinkAccessStatsDO linkAccessStatsDO : listStatsByShortLink) {
            // 3.1 遍历每一条数据, 生成对应的每天访问数据
            ShortLinkStatsAccessDailyRespDTO shortLinkStatsAccessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                    .uv(linkAccessStatsDO.getUv())
                    .pv(linkAccessStatsDO.getPv())
                    .uip(linkAccessStatsDO.getUip())
                    .date(DateUtil.formatDate(linkAccessStatsDO.getDate()))
                    .build();
            // 3.2 将得到的数据添加到daily中
            daily.add(shortLinkStatsAccessDailyRespDTO);
            // 3.3 将对应的日期map设为true
            dateIsNotNull.put(DateUtil.formatDate(linkAccessStatsDO.getDate()), true);
        }
        // 4. 遍历dateIsNotNull, 将没有数据的日期初始化为数据都为0的ShortLinkStatsAccessDailyRespDTO
        for (Map.Entry<String, Boolean> entry : dateIsNotNull.entrySet()) {
            if (!entry.getValue()){
                // 4.1 如果对应日期的值为false, 标识数据库没有当前日期对应数据
                ShortLinkStatsAccessDailyRespDTO shortLinkStatsAccessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                        .uv(0)
                        .pv(0)
                        .uip(0)
                        .date(entry.getKey())
                        .build();
                // 4.2 将得到的默认值添加到daily中
                daily.add(shortLinkStatsAccessDailyRespDTO);
            }
        }
        //5. 排序daily并添加到响应中
        daily.sort(new Comparator<ShortLinkStatsAccessDailyRespDTO>() {
            @Override
            public int compare(ShortLinkStatsAccessDailyRespDTO o1, ShortLinkStatsAccessDailyRespDTO o2) {
                return DateUtil.compare(DateUtil.parseDate(o1.getDate()), DateUtil.parseDate(o2.getDate()));
            }
        });
        result.setDaily(daily);

        // *****地区信息统计*****
        List<ShortLinkStatsLocaleCNRespDTO> localResult = new ArrayList<>();
        // 1. 查询数据库中指定日期内的短链接访问位置信息
        List<LinkLocalStatsDO> localStats = linkLocalStatsMapper.listLocalByShortLink(shortLinkStatsReqDTO);
        // 2. 求所有省份的访问量之和
        int localStatsSum = localStats.stream().mapToInt(LinkLocalStatsDO::getCnt).sum();
        // 3. 求出每个省份对应的百分比
        for (LinkLocalStatsDO localStat : localStats) {
            // 3.1 计算百分比
            double ratio = Math.round(((double) localStat.getCnt() / localStatsSum) * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO build = ShortLinkStatsLocaleCNRespDTO.builder()
                    .cnt(localStat.getCnt())
                    .locale(localStat.getProvince())
                    .ratio(ratio)
                    .build();
            localResult.add(build);
        }
        // 4. 将得到的所有位置访问信息存入总体结果
        result.setLocaleCnStats(localResult);

        // *****小时访问详情*****, 这个功能的含义貌似是看每个小时的流量分布情况
        List<Integer> hourResult = new ArrayList<>();
        // 1. 查询数据库数据
        List<LinkAccessStatsDO> hourStats = linkAccessStatsMapper.listHourStatsByShortLink(shortLinkStatsReqDTO);
        // 2. 创建小时数据是否为空map
        Map<Integer, Integer> hourIsNotNull = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            hourIsNotNull.put(i, -1);
        }
        // 3. 根据hourStats更新hourIsNotNull
        for (LinkAccessStatsDO hourStat : hourStats) {
            hourIsNotNull.put(hourStat.getHour(), hourStat.getPv());
        }
        // 4. 根据hourIsNotNull更新hourResult
        for (Map.Entry<Integer, Integer> entry : hourIsNotNull.entrySet()) {
            if (entry.getValue() > -1){
                hourResult.add(entry.getValue());
            }else {
                hourResult.add(0);
            }
        }
        //  5. 将hourResult添加到总体结果中
        result.setHourStats(hourResult);

        // *****高频IP统计*****
        List<ShortLinkStatsTopIpRespDTO> topResult = new ArrayList<>();
        List<Map<String, Object>> topIpStats = linkAccessLogsMapper.listTopIpByShortLink(shortLinkStatsReqDTO);
        for (Map<String, Object> topIpStat : topIpStats) {
            ShortLinkStatsTopIpRespDTO build = ShortLinkStatsTopIpRespDTO.builder()
                    .ip(topIpStat.get("ip").toString())
                    .cnt(Integer.parseInt(topIpStat.get("count").toString()))
                    .build();
            topResult.add(build);
        }
        result.setTopIpStats(topResult);

        // *****周内统计详情*****
        List<Integer> weekdayResult = new ArrayList<>();
        List<LinkAccessStatsDO> weekdayStats = linkAccessStatsMapper.listWeekdaySatatsByShortLink(shortLinkStatsReqDTO);
        Map<Integer, Integer> weekdayIsNotNull = new LinkedHashMap<>();
        for (int i = 1; i < 8; i++) {
            weekdayIsNotNull.put(i, -1);
        }
        for (LinkAccessStatsDO weekdayStat : weekdayStats) {
            weekdayIsNotNull.put(weekdayStat.getWeekday(), weekdayStat.getPv());
        }
        for (Map.Entry<Integer, Integer> entry : weekdayIsNotNull.entrySet()) {
            if (entry.getValue() > -1){
                weekdayResult.add(entry.getValue());
            }else {
                weekdayResult.add(0);
            }
        }
        result.setWeekdayStats(weekdayResult);

        // *****浏览器访问统计*****
        List<ShortLinkStatsBrowserRespDTO> browserResult = new ArrayList<>();
        List<LinkBrowserStatsDO> browserStats = linkBrowserStatsMapper.listBrowserStatsByShortLink(shortLinkStatsReqDTO);
        int browserSum = browserStats.stream().mapToInt(LinkBrowserStatsDO::getCnt).sum();
        for (LinkBrowserStatsDO browserStat : browserStats) {
            double ratio = ( (double) browserStat.getCnt() / browserSum ) * 100.0 / 100.0;
            ShortLinkStatsBrowserRespDTO build = ShortLinkStatsBrowserRespDTO.builder()
                    .browser(browserStat.getBrowser())
                    .cnt(browserStat.getCnt())
                    .ratio(ratio)
                    .build();
            browserResult.add(build);
        }
        result.setBrowserStats(browserResult);

        // *****操作系统访问详情*****
        List<ShortLinkStatsOsRespDTO> osResult = new ArrayList<>();
        List<LinkOsStatsDO> osStats = linkOsStatsMapper.listOsStatsByShortLink(shortLinkStatsReqDTO);
        int osSum = osStats.stream().mapToInt(LinkOsStatsDO::getCnt).sum();
        for (LinkOsStatsDO osStat : osStats) {
            double ratio = ( (double) osStat.getCnt() / osSum ) * 100.0 / 100.0;
            ShortLinkStatsOsRespDTO build = ShortLinkStatsOsRespDTO.builder()
                    .os(osStat.getOs())
                    .cnt(osStat.getCnt())
                    .ratio(ratio)
                    .build();
            osResult.add(build);
        }
        result.setOsStats(osResult);

        // *****访客类型数据统计*****
        List<ShortLinkStatsUvRespDTO> uvTypeResult = new ArrayList<>();
        Map<String, Object> uvTypeStats = linkAccessLogsMapper.findUvTypeCntByShortLink(shortLinkStatsReqDTO);
        // 1. 获取新老访客的数量
        int oldUserCnt = 0;
        int newUserCnt = 0;
        if (uvTypeStats != null){
            oldUserCnt = Integer.parseInt(uvTypeStats.get("oldUserCnt").toString());
            newUserCnt = Integer.parseInt(uvTypeStats.get("newUserCnt").toString());
        }
        // 2. 统计新老访客的比例
        int uvSum = oldUserCnt + newUserCnt;
        double newUserRatio = ((double) newUserCnt / uvSum) * 100.0 / 100.0;
        double oldUserRatio = ((double) oldUserCnt / uvSum) * 100.0 / 100.0;
        // 3. 封装DTO
        ShortLinkStatsUvRespDTO newUser = ShortLinkStatsUvRespDTO.builder()
                .uvType("newUser")
                .cnt(newUserCnt)
                .ratio(newUserRatio)
                .build();
        uvTypeResult.add(newUser);
        ShortLinkStatsUvRespDTO oldUser = ShortLinkStatsUvRespDTO.builder()
                .uvType("oldUser")
                .cnt(oldUserCnt)
                .ratio(oldUserRatio)
                .build();
        uvTypeResult.add(oldUser);
        result.setUvTypeStats(uvTypeResult);

        // *****访问设备统计*****
        List<ShortLinkStatsDeviceRespDTO> deviceResult = new ArrayList<>();
        List<LinkDeviceStatsDO> deviceStats = linkDeviceStatsMapper.listDeviceStatsByShortUrl(shortLinkStatsReqDTO);
        int deviceSum = deviceStats.stream().mapToInt(LinkDeviceStatsDO::getCnt).sum();
        for (LinkDeviceStatsDO deviceStat : deviceStats) {
            double ratio = ( (double) deviceStat.getCnt() / deviceSum ) * 100.0 / 100.0;
            ShortLinkStatsDeviceRespDTO build = ShortLinkStatsDeviceRespDTO.builder()
                    .device(deviceStat.getDevice())
                    .cnt(deviceStat.getCnt())
                    .ratio(ratio)
                    .build();
            deviceResult.add(build);
        }
        result.setDeviceStats(deviceResult);

        // *****访问网络类型统计*****
        List<ShortLinkStatsNetworkRespDTO> networkResult = new ArrayList<>();
        List<LinkNetworkStatsDO> networkStats = linkNetworkStatsMapper.listNetworkStatsByShortLink(shortLinkStatsReqDTO);
        int networkSum = networkStats.stream().mapToInt(LinkNetworkStatsDO::getCnt).sum();
        for (LinkNetworkStatsDO networkStat : networkStats) {
            double ratio = ( (double) networkStat.getCnt() / deviceSum ) * 100.0 / 100.0;
            ShortLinkStatsNetworkRespDTO build = ShortLinkStatsNetworkRespDTO.builder()
                    .network(networkStat.getNetwork())
                    .cnt(networkStat.getCnt())
                    .ratio(ratio)
                    .build();
            networkResult.add(build);
        }
        result.setNetworkStats(networkResult);

        // *****整体基础访问数据统计*****
        LinkAccessStatsDO linkAccessStatsDO = linkAccessLogsMapper.findPvUvUipByShortLink(shortLinkStatsReqDTO);
        result.setPv(linkAccessStatsDO.getPv());
        result.setUv(linkAccessStatsDO.getUv());
        result.setUip(linkAccessStatsDO.getUip());
        return result;
    }

    @Override
    public IPage<ShortLinkStatsAccessLogRespDTO> shortLinkStatsAccessLog(ShortLinkStatsAccessLogReqDTO accessLogReqDTO) {
        // 1. 分页查询LinkAccessLogs表
        // 1.1 设置查询条件
        LambdaQueryWrapper<LinkAccessLogsDO> wrapper = new LambdaQueryWrapper<>(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getFullShortUrl, accessLogReqDTO.getFullShortUrl())
                .eq(LinkAccessLogsDO::getGid, accessLogReqDTO.getGid())
                .between(LinkAccessLogsDO::getCreateTime, accessLogReqDTO.getStartDate(), accessLogReqDTO.getEndDate())
                .eq(LinkAccessLogsDO::getDelFlag, 0)
                .orderByDesc(LinkAccessLogsDO::getCreateTime);
        // 1.2 获取分页实体
        Page<LinkAccessLogsDO> page = new Page<>(accessLogReqDTO.getCurrent(), accessLogReqDTO.getSize());
        // 1.3 获取查询结果
        Page<LinkAccessLogsDO> linkAccessLogsDOPage = linkAccessLogsMapper.selectPage(page, wrapper);

        // 2. 转换查询结果
        // 2.1 首先确认返回类型
        List<LinkAccessLogsDO> linkAccessLogsDOS = linkAccessLogsDOPage.getRecords();
        List<ShortLinkStatsAccessLogRespDTO> respDTOS = new ArrayList<>(linkAccessLogsDOS.size());
        Set<String> users = new LinkedHashSet<>();
        for (LinkAccessLogsDO linkAccessLogsDO : linkAccessLogsDOS) {
            // 2.2 解析分页查询的结果, 将得到的DO值转换为respDTO同时判断用户类型
            respDTOS.add(BeanUtil.toBean(linkAccessLogsDO, ShortLinkStatsAccessLogRespDTO.class));
            // 2.3 同时获取记录中的访客
            users.add(linkAccessLogsDO.getUser());
        }

        // 3. 判断用户类型
        List<Map<String, Object>> uvTypeList = linkAccessLogsMapper.selectUvTypeByUsers(accessLogReqDTO, users);
        Map<String, String> user2Type = new HashMap<>();
        for (Map<String, Object> uvType : uvTypeList) {
            user2Type.put(uvType.get("user").toString(), uvType.get("uvType").toString());
        }
        for (ShortLinkStatsAccessLogRespDTO respDTO : respDTOS) {
            respDTO.setUvType(user2Type.get(respDTO.getUser()));
        }

        // 4. 生成返回结果
        Page<ShortLinkStatsAccessLogRespDTO> result = new Page<>();
        BeanUtil.copyProperties(linkAccessLogsDOPage, result);
        result.setRecords(respDTOS);
        return result;
    }


}
