package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkDeviceStatsDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {
    @Insert("insert into t_link_device_stats (full_short_url, gid, date, cnt, device, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today},#{cnt}, #{device}, NOW(), NOW(), 0)" +
            "on duplicate key update cnt = cnt + #{cnt}")
    void updateDeviceStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                          @Param("cnt") int cnt, @Param("device") String device);

    /**
     * 根据短链接查询设备访问数量
     */
    @Select("select device, sum(cnt) as cnt " +
            "from t_link_device_stats " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, device;")
    List<LinkDeviceStatsDO> listDeviceStatsByShortUrl(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 根据分组查询设备访问数量
     */
    @Select("select device, sum(cnt) as cnt " +
            "from t_link_device_stats " +
            "where " +
            "gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by gid, device;")
    List<LinkDeviceStatsDO> listDeviceStatsByGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
