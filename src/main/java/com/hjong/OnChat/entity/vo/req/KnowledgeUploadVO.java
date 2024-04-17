package com.hjong.OnChat.entity.vo.req;

import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/17
 **/

@Data
public class KnowledgeUploadVO {

    //知识库id
    private Integer id;
    private FilePart file;
}
