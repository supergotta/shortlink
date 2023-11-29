package com.supergotta.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.supergotta.shortlink.admin.common.convention.result.Results;
import com.supergotta.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.supergotta.shortlink.admin.common.exception.ClientException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    private static final List<String> IGNORE_IRL = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();

        // 判断当前请求是否在“无需拦截”的名单中
        if (!IGNORE_IRL.contains(requestURI)) {
            // 如果不在, 则执行这段逻辑
            String method = httpServletRequest.getMethod();
            // 判断当前请求是否为注册用户请求
            if (!(Objects.equals(requestURI, "/api/short-link/admin/v1/user") && Objects.equals(method, "POST"))) {
                // 不是用户注册请求, 则正常执行拦截逻辑
                // 首先取出请求头中的username和token
                String username = httpServletRequest.getHeader("username");
                String token = httpServletRequest.getHeader("token");
                // 判断username和token是否成功获取了
                if (!StrUtil.isAllNotBlank(username, token)) {
                    // 这里由于全局异常处理器不能不能捕获这里的异常, 所以这里采用特殊处理
                    /*throw new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL);*/
                    returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
                    return;
                }
                // 根据这个token和username去redis中查询此用户的详细信息
                Object userInfoJsonStr;
                try {
                    // 判断redis的查询结果是否为空
                    userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);
                    if (userInfoJsonStr == null) {
                        // 如果为空, 抛出异常
                        returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
                        return;
                    }
                } catch (Exception ex) {
                    returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
                    return;
                }
                // 如果不为空, 将用户信息放入ThreadLocal中
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

    /**
     * 自定义返回客户端Json
     */
    private void returnJson(HttpServletResponse response, String json){
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        }catch (IOException e){
        }finally {
            if (writer != null){
                writer.close();
            }
        }
    }
}
