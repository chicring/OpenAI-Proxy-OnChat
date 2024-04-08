package com.hjong.OnChat.entity.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant registerDate;
}
