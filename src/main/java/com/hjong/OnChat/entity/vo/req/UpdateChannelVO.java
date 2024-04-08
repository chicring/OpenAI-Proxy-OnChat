package com.hjong.OnChat.entity.vo.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/29
 **/
@Data
public class UpdateChannelVO {

    @NotEmpty(message = "name不能为空")
    private Integer id;
    @NotEmpty(message = "name不能为空")
    private String name;
    @NotEmpty(message = "key不能为空")
    private String apiKey;
    @NotEmpty(message = "url不能为空")
    private String baseUrl;
    @NotEmpty(message = "模型不能为空")
    private String models;
    private Integer priority;
    private boolean enableProxy;
}
