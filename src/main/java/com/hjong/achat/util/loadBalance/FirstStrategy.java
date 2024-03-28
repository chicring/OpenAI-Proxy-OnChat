package com.hjong.achat.util.loadBalance;

import com.hjong.achat.entity.DTO.Channel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/26
 **/
@Component
public class FirstStrategy implements Strategy{
    @Override
    public Channel execute(List<Channel> channelList) {
        return channelList.getFirst();
    }
}
