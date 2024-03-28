package com.hjong.achat.entity.VO.resp;

import com.hjong.achat.entity.DTO.Server;
import lombok.Data;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/27
 **/



@Data
public class StatusInfoVO {
    long UserAmount;
    long ChannelAmount;

    Server server;
}
