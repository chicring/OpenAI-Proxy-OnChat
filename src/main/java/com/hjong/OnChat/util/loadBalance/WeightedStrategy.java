package com.hjong.OnChat.util.loadBalance;

import com.hjong.OnChat.entity.dto.Channel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component("weight")
public class WeightedStrategy implements Strategy{

    @Override
    public Channel execute(List<Channel> channelList) {

        int totalWeight = channelList.stream().mapToInt(Channel::getPriority).sum();

        AtomicInteger randomNum = new AtomicInteger(new Random().nextInt(totalWeight));

        return channelList.stream()
                .filter(channel -> randomNum.addAndGet(-channel.getPriority()) < 0)
                .findFirst()
                .orElse(null);
    }
}
