package com.hjong.OnChat.util.loadBalance;

import com.hjong.OnChat.entity.dto.Channel;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/26
 **/
public interface Strategy {
    Channel execute(List<Channel> channelList);
}
