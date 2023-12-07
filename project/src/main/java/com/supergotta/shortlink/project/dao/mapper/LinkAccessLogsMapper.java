package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.supergotta.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkGroupStatsAccessLogReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsAccessLogReqDTO;
import com.supergotta.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {
    /**
     * 根据短链接搜索ip访问记录前五
     */
    @Select("select ip, count(ip) as count " +
            "from t_link_access_logs " +
            "where " +
            "full_short_url = #{fullShortUrl} " +
            "and gid = #{gid} " +
            "and create_time between #{startDate} and #{endDate} " +
            "group by full_short_url, gid, ip " +
            "order by count desc " +
            "limit 5;")
    List<Map<String, Object>> listTopIpByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 根据分组标识搜索ip访问记录前五
     */
    @Select("select ip, count(ip) as count " +
            "from t_link_access_logs " +
            "where " +
            "gid = #{gid} " +
            "and create_time between #{startDate} and #{endDate} " +
            "group by gid, ip " +
            "order by count desc " +
            "limit 5;")
    List<Map<String, Object>> listTopIpByShortGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);

    /**
     * 根据短链接查询访客类型统计数据
     */
    @Select("select sum(old_user) as oldUserCnt, sum(new_user) as newUserCnt " +
            "from " +
            "(select " +
            "case when count(distinct date(create_time)) > 1 then 1 else 0 end as old_user, " +
            "case when count(distinct date(create_time)) = 1 and max(create_time) >= #{startDate} and max(create_time) <= #{endDate} then 1 else 0 end as new_user " +
            "from t_link_access_logs " +
            "where full_short_url = #{fullShortUrl} and gid = #{gid} " +
            "group by user)" +
            "as user_counts;")
    Map<String, Object> findUvTypeCntByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 根据分组查询访客类型统计数据
     */
    @Select("select sum(old_user) as oldUserCnt, sum(new_user) as newUserCnt " +
            "from " +
            "(select " +
            "case when count(distinct date(create_time)) > 1 then 1 else 0 end as old_user, " +
            "case when count(distinct date(create_time)) = 1 and max(create_time) >= #{startDate} and max(create_time) <= #{endDate} then 1 else 0 end as new_user " +
            "from t_link_access_logs " +
            "where gid = #{gid} " +
            "group by user)" +
            "as user_counts;")
    Map<String, Object> findUvTypeCntByGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);

    /**
     * 根据分组标识统计pv、uv、uip
     */
    @Select("select count(user) as pv, count(distinct user) as uv, count(distinct ip) as uip " +
            "from t_link_access_logs " +
            "where " +
            "gid = #{gid} " +
            "and create_time between #{startDate} and #{endDate} ")
    LinkAccessStatsDO findPvUvUipByGroup(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 根据短链接统计总体pv、uv、uip
     */
    @Select("select count(user) as pv, count(distinct user) as uv, count(distinct ip) as uip " +
            "from t_link_access_logs " +
            "where " +
            "gid = #{gid} " +
            "and full_short_url = #{fullShortUrl} " +
            "and create_time between #{startDate} and #{endDate} " +
            "group by full_short_url, gid")
    LinkAccessStatsDO findPvUvUipByShortLink(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 根据短链接统计总体pv、uv、uip
     */
    @Select("select count(user) as pv, count(distinct user) as uv, count(distinct ip) as uip " +
            "from t_link_access_logs " +
            "where " +
            "gid = #{gid} " +
            "and create_time between #{startDate} and #{endDate} " +
            "group by gid")
    LinkAccessStatsDO findPvUvUipByShortGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);

    /**
     * 根据信息判断用户列表中用户是否为新老用户
     * @param accessLogReqDTO 判断条件
     * @param users 用户列表
     * @return 用户类型列表
     */
    /*@MapKey("user")*/
    List<Map<String, Object>> selectUvTypeByUsers(@Param("accessLogReqDTO")ShortLinkStatsAccessLogReqDTO accessLogReqDTO,
                                                  @Param("users") Set<String> users);

    List<Map<String, Object>> selectGroupUvTypeByUsers(@Param("accessLogReqDTO") ShortLinkGroupStatsAccessLogReqDTO accessLogReqDTO,
                                                       @Param("users")Set<String> users);
}
