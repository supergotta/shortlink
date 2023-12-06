package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkBrowserStatsDO;
import com.supergotta.shortlink.project.dao.entity.LinkNetworkStatsDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkNetworkStatsMapper extends BaseMapper<LinkBrowserStatsDO> {
    @Insert("insert into t_link_network_stats (full_short_url, gid, date, cnt, network, create_time, update_time, del_flag) " +
            "values (#{fullShortUrl}, #{gid}, #{today},#{cnt}, #{network}, NOW(), NOW(), 0)" +
            "on duplicate key update cnt = cnt + #{cnt}")
    void updateNetworkStats(@Param("fullShortUrl") String fullShortUrl, @Param("gid") String gid, @Param("today") LocalDate today,
                          @Param("cnt") int cnt, @Param("network") String network);

    /**
     * 根据短链接查询网络类型统计数据
     */
    @Select("select network, sum(cnt) as cnt " +
            "from t_link_network_stats " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, network;")
    List<LinkNetworkStatsDO> listNetworkStatsByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     *  根据分组标识查询网络类型统计数据
     */
    @Select("select network, sum(cnt) as cnt " +
            "from t_link_network_stats " +
            "where " +
            "gid = #{gid} " +
            "and date between #{startDate} and #{endDate} " +
            "group by gid, network;")
    List<LinkNetworkStatsDO> listNetworkStatsByGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
