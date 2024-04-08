package com.hjong.OnChat.entity.vo.resp;

import lombok.Builder;
import lombok.Data;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
@Builder
@Data
public class TokenInfoVO {

    private String token;
    private Integer userId;
    private String role;
    private long expiresAt;
}
