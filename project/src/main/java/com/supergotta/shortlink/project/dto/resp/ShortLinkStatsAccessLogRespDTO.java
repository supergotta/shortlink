package com.supergotta.shortlink.project.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 短链接监控访问日志响应参数
 */
@Data
public class ShortLinkStatsAccessLogRespDTO {

    /**
     * 访客类型
     */
    private String uvType;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 所在网络类型
     */
    private String network;

    /**
     * 访问设备
     */
    private String device;

    /**
     * 地区信息
     */
    private String locale;

    /**
     * 用户信息
     */
    private String user;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
