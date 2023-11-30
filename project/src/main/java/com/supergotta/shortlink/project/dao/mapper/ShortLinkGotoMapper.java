package com.supergotta.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supergotta.shortlink.project.dao.entity.ShortLinkGotoDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接路由持久层
 */
@Mapper
public interface ShortLinkGotoMapper extends BaseMapper<ShortLinkGotoDO> {
}
