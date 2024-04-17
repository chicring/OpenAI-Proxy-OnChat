package com.hjong.OnChat.chain.loader;

import java.io.InputStream;
import java.util.List;

/**
 * 加载资源
 **/

public interface ResourceLoader {

    /**
     * 获取资源内容
     * @param inputStream 文件输入流
     * @return pdf 原始内容
     */
    String getContent(InputStream inputStream);

    /**
     * 获取分块内容
     * @param content pdf原始内容
     * @return 分块内容,切割后的内容
     */
    List<String> getChunkList(String content);
}
