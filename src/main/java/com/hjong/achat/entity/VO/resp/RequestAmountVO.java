package com.hjong.achat.entity.VO.resp;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/25
 **/
@Data
public class RequestAmountVO {

    Long total;
    List<item> list;

    @Data
    public static class item {
        String date;
        Long count;
    }

}
