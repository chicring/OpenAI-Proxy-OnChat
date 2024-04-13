package com.hjong.OnChat.chain.vectorizer.zhipu;

import lombok.Data;


import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/
@Data
public class ResponseBody {

    String model;
    String object;
    List<Datas> data;
    Usage usage;

    @Data
    public static class Datas {
        Integer index;
        String object;
        List<Double> embedding;
    }

    @Data
    public static class Usage {
       Integer completion_tokens;
       Integer prompt_tokens;
       Integer total_tokens;
    }
}
