package com.hjong.OnChat.entity.vo.resp;

import lombok.Data;
import org.springframework.data.annotation.Id;


@Data
public class ChannelPermissionVO {
    @Id
    private Integer id;
    private Integer user_id;
    private Integer channel_id;
    private String username;
    private String channelName;
    private String channelType;
}
