package com.hjong.achat.entity.VO.resp;

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
