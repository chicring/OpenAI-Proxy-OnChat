package com.hjong.OnChat.entity.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/18
 **/

@Table("Files")
@Data
public class File {
    @Id
    private Integer id;
    private Instant createTime;
    private Instant updateTime;
    private String name;
    private Integer status;
    private Integer knowledgeBaseId;
}
