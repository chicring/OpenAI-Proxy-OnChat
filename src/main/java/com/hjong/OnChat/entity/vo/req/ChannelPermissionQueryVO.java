package com.hjong.OnChat.entity.vo.req;


import com.hjong.OnChat.entity.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelPermissionQueryVO extends PageRequest {
    private String username;
    private String channelName;
}
