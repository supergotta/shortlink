package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    @Insert("insert into t_link_access_stats (full_short_url, gid, date, pv, uv, uip , hour, weekday, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today}, #{pv}, #{uv}, #{uip}, #{hour}, #{weekday}, NOW(), NOW(), 0)" +
            "on duplicate key update pv = pv + #{pv}, uv = uv + #{uv}, uip = uip + #{uip}")
    void updateStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                     @Param("pv") int pv,@Param("uv") int uv,@Param("uip") int uip,
                     @Param("hour")int hour,@Param("weekday") int weekday);
}
