package com.hjong.OnChat.entity.vo.req;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.codec.multipart.FilePart;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/

@Data
public class KnowledgeBaseVO {

    @Length(min = 1, max = 20)
    private String name;

    private String description;
    @Pattern(regexp = "^[a-zA-Z]{3,8}$")
    private String collectionName;
}
