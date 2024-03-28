package com.hjong.achat.util.loadBalance;

import com.hjong.achat.entity.DTO.Channel;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/26
 **/
public interface Strategy {
    Channel execute(List<Channel> channelList);
}
