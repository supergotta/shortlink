package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkLocalStatsDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkLocalStatsMapper extends BaseMapper<LinkLocalStatsDO> {
    @Insert("insert into t_link_local_stats (full_short_url, gid, date, cnt, province, city, adcode, country, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today},#{cnt}, #{province}, #{city}, #{adcode}, #{country}, NOW(), NOW(), 0)" +
            "on duplicate key update cnt = cnt + #{cnt}")
    void updateLocalStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                          @Param("cnt") int cnt, @Param("province") String province, @Param("city") String city,
                          @Param("adcode") String adcode, @Param("country") String country);


    /**
     * 根据短链接查询制定日期内访问位置信息
     */
    @Select("select province, sum(cnt) as cnt " +
            "from t_link_local_stats " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, province")
    List<LinkLocalStatsDO> listLocalByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);
}
