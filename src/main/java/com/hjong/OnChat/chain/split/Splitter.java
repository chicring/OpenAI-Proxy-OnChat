package com.hjong.OnChat.chain.split;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/
public interface Splitter {

    /**
     * 分割内容
     * @param content 原始内容
     * @return 分割后的内容
     */
    List<String> split(String content);
}
