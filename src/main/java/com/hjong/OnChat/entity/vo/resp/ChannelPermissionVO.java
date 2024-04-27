package com.hjong.OnChat.entity.vo.resp;

import lombok.Data;

@Data
public class ChannelPermissionVO {
    private Integer id;
    private Integer userId;
    private Integer channelId;
    private String username;
    private String channelName;
    private String channelType;
}
