package com.hjong.OnChat.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/

@Table("knowledgeBase")
@Data
public class KnowledgeBase {
    @Id
    private Integer id;
    private Integer userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant updateTime;
    private String name;
    private String description;
    private String collectionName;
}
