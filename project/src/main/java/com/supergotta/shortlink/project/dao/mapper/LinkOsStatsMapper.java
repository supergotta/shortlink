package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkOsStatsDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {
    @Insert("insert into t_link_os_stats (full_short_url, gid, date, cnt, os, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today},#{cnt}, #{os}, NOW(), NOW(), 0)" +
            "on duplicate key update cnt = cnt + #{cnt}")
    void updateOsStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                          @Param("cnt") int cnt, @Param("os") String os);

    /**
     * 根据短链接查询操作系统对应的访问数
     */
    @Select("select os, sum(cnt) as cnt " +
            "from t_link_os_stats " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, os;")
    List<LinkOsStatsDO> listOsStatsByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);
}
