package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkBrowserStatsDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {
    @Insert("insert into t_link_browser_stats (full_short_url, gid, date, cnt, browser, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today},#{cnt}, #{browser}, NOW(), NOW(), 0)" +
            "on duplicate key update cnt = cnt + #{cnt}")
    void updateBrowserStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                          @Param("cnt") int cnt, @Param("browser") String browser);

    /**
     * 通过短链接查询每种浏览器对应的访问数
     */
    @Select("select browser, sum(cnt) as cnt " +
            "from t_link_browser_stats " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, browser")
    List<LinkBrowserStatsDO> listBrowserStatsByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 通过分组查询每种浏览器对应的访问数
     */
    @Select("select browser, sum(cnt) as cnt " +
            "from t_link_browser_stats " +
            "where " +
            "gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by gid, browser")
    List<LinkBrowserStatsDO> listBrowserStatsBySGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
