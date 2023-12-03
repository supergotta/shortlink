package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface LinkNetworkStatsMapper extends BaseMapper<LinkBrowserStatsDO> {
    @Insert("insert into t_link_network_stats (full_short_url, gid, date, cnt, network, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today},#{cnt}, #{network}, NOW(), NOW(), 0)" +
            "on duplicate key update cnt = cnt + #{cnt}")
    void updateNetworkStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                          @Param("cnt") int cnt, @Param("network") String network);
}
