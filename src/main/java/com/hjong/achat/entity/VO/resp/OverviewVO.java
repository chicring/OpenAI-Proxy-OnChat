package com.hjong.achat.entity.VO.resp;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/26
 **/
@Data
public class OverviewVO {


    List<Day> overview;

    @Data
    public static class Day {
        String date;
        List<Usage> usages;
    }
    @Data
    public static class Usage {
        String model;
        Long count;
    }
}
