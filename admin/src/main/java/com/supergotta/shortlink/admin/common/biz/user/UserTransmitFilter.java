package com.supergotta.shortlink.admin.common.biz.user;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    private static final List<String> IGNORE_IRL = Lists.newArrayList(
            "/api/short-link/v1/user/login",
            "/api/short-link/v1/user/has-username"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();

        if (!IGNORE_IRL.contains(requestURI)){
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            Object userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);

            if (userInfoJsonStr != null){
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
