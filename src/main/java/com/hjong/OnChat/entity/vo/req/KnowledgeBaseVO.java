package com.hjong.OnChat.entity.vo.req;

import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/

@Data
public class KnowledgeBaseVO {
    private Integer userId;
    private String name;
    private String description;
    private String vectorCollectionName;
    private FilePart file;
}
