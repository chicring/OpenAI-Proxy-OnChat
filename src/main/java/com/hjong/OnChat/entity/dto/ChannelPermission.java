package com.hjong.OnChat.entity.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/13
 **/
@Table("ChannelPermission")
@Data
public class ChannelPermission {
    @Id
    private Integer id;
    private Integer user_id;
    private Integer channel_id;
}
