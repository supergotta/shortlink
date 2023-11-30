package com.supergotta.shortlink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.supergotta.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.supergotta.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {

    /**
     * 远程调用创建短链接
     * @param shortLinkCreateReqDTO
     * @return
     */
    default String createShortLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO){
        return HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(shortLinkCreateReqDTO));
    }

    /**
     * 远程调用查询短链接
     * @param gid
     * @param current
     * @param size
     * @return
     */
    default String pageShortLink(
            @RequestParam("gid") String gid,
            @RequestParam("current") String current,
            @RequestParam("size") String size){
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", gid);
        requestMap.put("current", current);
        requestMap.put("size", size);
        return HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
    }

    /**
     * 查询分组短链接总量
     * @return
     */
    default String listGroupShortLinkCount(List<String>gids){
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gids", gids);
        String response = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", requestMap);
        return response;
    }

    /**
     * 修改短链接
     */
    default void updateShortLink(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO){
        String post = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/update", JSON.toJSONString(shortLinkUpdateReqDTO));
    }
}
