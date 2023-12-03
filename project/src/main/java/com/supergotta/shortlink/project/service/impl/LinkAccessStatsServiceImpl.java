package com.supergotta.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supergotta.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.supergotta.shortlink.project.dao.mapper.LinkAccessStatsMapper;
import com.supergotta.shortlink.project.service.LinkAccessStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkAccessStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStatsDO> implements LinkAccessStatsService {


}
