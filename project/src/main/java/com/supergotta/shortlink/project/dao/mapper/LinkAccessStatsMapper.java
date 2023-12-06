package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    @Insert("insert into t_link_access_stats (full_short_url, gid, date, pv, uv, uip , hour, weekday, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today}, #{pv}, #{uv}, #{uip}, #{hour}, #{weekday}, NOW(), NOW(), 0)" +
            "on duplicate key update pv = pv + #{pv}, uv = uv + #{uv}, uip = uip + #{uip}")
    void updateStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                     @Param("pv") int pv,@Param("uv") int uv,@Param("uip") int uip,
                     @Param("hour")int hour,@Param("weekday") int weekday);

    /**
     * 根据短链接查询日期内基础数据
     */
    @Select("select date, sum(pv) as pv, sum(uv) as uv, sum(uip) as uip " +
            "from t_link_access_stats " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, date;")
    List<LinkAccessStatsDO> listStatsByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 根据分组标识查询日期内基础数据
     */
    @Select("select date, sum(pv) as pv, sum(uv) as uv, sum(uip) as uip " +
            "from t_link_access_stats " +
            "where " +
            "gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by gid, date;")
    List<LinkAccessStatsDO> listStatsByGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);

    /**
     * 查询日期内每小时数据
     */
    @Select("select hour, sum(pv) as pv " +
            "from t_link_access_stats " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, hour;")
    List<LinkAccessStatsDO> listHourStatsByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 查询日期内每小时数据
     */
    @Select("select hour, sum(pv) as pv " +
            "from t_link_access_stats " +
            "where " +
            "gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by gid, hour;")
    List<LinkAccessStatsDO> listHourStatsByGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);

    /**
     * 根据短链接查询周内访问详情
     */
    @Select("select weekday, sum(pv) as pv " +
            "from t_link_access_stats " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, weekday;")
    List<LinkAccessStatsDO> listWeekdaySatatsByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 根据分组标识查询周内访问详情
     */
    @Select("select weekday, sum(pv) as pv " +
            "from t_link_access_stats " +
            "where " +
            "gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by gid, weekday;")
    List<LinkAccessStatsDO> listWeekdaySatatsByGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
