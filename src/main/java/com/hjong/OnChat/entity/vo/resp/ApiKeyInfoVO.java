package com.hjong.OnChat.entity.vo.resp;

import lombok.Data;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
@Data
public class ApiKeyInfoVO {
    private String name;
    private String apiKey;
    private Long createdAt;
    private Long expiresAt;
}
