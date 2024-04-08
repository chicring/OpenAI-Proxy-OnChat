package com.hjong.OnChat.entity.dto.overview;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/5
 **/
@Data
public class TotalModelUsageRatio {

    long total;

    @JsonFormat(pattern = "yyyy-MM",timezone = "GMT+8")
    Instant date;

    Map<String, Long> ratio;
}
