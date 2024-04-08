package com.hjong.OnChat.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/

@Table("logs")
@Data
@Accessors(chain = true)
public class Logs {
    @Id
    private Long id;
    private Integer channelId;
    private String channelType;
    private String channelName;
    private Integer userId;
    private String username;
    private String model;
    private Long totalToken;
    private Long promptTokens;
    private Long completionTokens;
    private String inputText;
    private String outputText;
    private String ip;
    private double consumeTime;
    private Long createdAt;
}
