package com.hjong.achat.entity.VO.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/

@Data
public class addChannelVO {
    @NotEmpty(message = "name不能为空")
    private String name;

    @NotEmpty(message = "类型不能为空")
    private String type;

    @NotEmpty(message = "key不能为空")
    private String apiKey;

    @NotEmpty(message = "url不能为空")
    private String baseUrl;

    @NotEmpty(message = "模型不能为空")
    private String models;

    private Integer priority;

    private boolean enableProxy;
}
