package com.hjong.OnChat.entity.dto.overview;

import lombok.Data;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/5
 **/
@Data
public class ApiRequestCount {
    Long total;
    List<ApiRequestCount.Item> list;

    @Data
    public static class Item {
        String date;
        Long count;
    }
}
