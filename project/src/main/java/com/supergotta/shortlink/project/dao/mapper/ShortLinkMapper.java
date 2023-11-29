package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.ShortLinkDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接持久层
 */
@Mapper
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {
}
