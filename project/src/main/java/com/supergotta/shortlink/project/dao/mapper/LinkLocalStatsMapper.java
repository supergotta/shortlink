package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkLocalStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface LinkLocalStatsMapper extends BaseMapper<LinkLocalStatsDO> {
    @Insert("insert into t_link_local_stats (full_short_url, gid, date, cnt, province, city, adcode, country, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today},#{cnt}, #{province}, #{city}, #{adcode}, #{country}, NOW(), NOW(), 0)" +
            "on duplicate key update cnt = cnt + #{cnt}")
    void updateLocalStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                          @Param("cnt") int cnt, @Param("province") String province, @Param("city") String city,
                          @Param("adcode") String adcode, @Param("country") String country);
}
