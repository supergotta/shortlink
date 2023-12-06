package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supergotta.shortlink.project.dao.entity.ShortLinkDO;
import com.supergotta.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 短链接持久层
 */
@Mapper
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    @Update("update t_link " +
            "set total_pv = total_pv + #{pv}, total_uv = total_uv + #{uv}, total_uip = total_uip + #{uip} " +
            "where full_short_url = #{fullShortLink} and gid = #{gid};")
    void incrementStats(@Param("fullShortLink") String fullShortLink,
                        @Param("gid") String gid,
                        @Param("pv") Integer pv,
                        @Param("uv") Integer uv,
                        @Param("uip") Integer uip);

    /**
     * 根据指定的排序方式分页查询分组内
     */
    IPage<ShortLinkDO> pageLink(ShortLinkPageReqDTO shortLinkPageReqDTO);
}
