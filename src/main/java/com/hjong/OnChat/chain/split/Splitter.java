package com.hjong.OnChat.chain.split;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/
public interface Splitter {

    List<String> split(String content);
}
