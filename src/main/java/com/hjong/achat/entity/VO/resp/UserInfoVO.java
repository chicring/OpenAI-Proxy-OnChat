package com.hjong.achat.entity.VO.resp;

import com.hjong.achat.enums.RoleType;
import lombok.Data;

import java.time.Instant;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/

@Data
public class UserInfoVO {
    private Integer id;
    private String username;
    private String email;
    private String role;
    private int currentUsage;
    private int totalUsage;
    private Instant registerDate;
}
