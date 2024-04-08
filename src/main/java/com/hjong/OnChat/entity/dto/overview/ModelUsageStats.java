package com.hjong.OnChat.entity.dto.overview;

import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/5
 **/

@Data
public class ModelUsageStats {

    List<Usage> list;

    @Data
    public static class Usage {
        String Date;
        Map<String, Long> usages;
    }

}
