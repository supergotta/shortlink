package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {


    /**
     * 根据短链接更新今日数据
     */
    @Insert("insert into t_link_stats_today " +
            "(full_short_url, gid, date, today_pv, today_uv, today_uip, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{date},#{todayPv}, #{todayUv}, #{todayUip}, NOW(), NOW(), 0)" +
            "on duplicate key " +
            "update today_pv = today_pv + #{todayPv}, " +
            "today_uv = today_uv + #{todayUv}, " +
            "today_uip = today_uip + #{todayUip}")
    void updateTodayStatsByShortLink(LinkStatsTodayDO linkStatsTodayDO);
}
