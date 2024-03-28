package com.hjong.achat.entity.VO.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/23
 **/
@Data
public class FindLogVO {
    private Integer channelId;
    private String channelType;
    private String channelName;
    private Integer userId;
    private String model;
    private String ip;
    private Long startTime;
    private Long endTime;

    @Min(value = 1, message = "page不能小于1")
    private Long page;

    @Min(value = 5, message = "size不能小于5")
    @Max(value = 20, message = "size不能大于20")
    private Long size;
}
