package com.supergotta.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortLinkGroupSaveReqDTO {

    /**
     * 分组名
     */
    private String name;
}
