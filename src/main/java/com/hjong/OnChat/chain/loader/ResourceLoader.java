package com.hjong.OnChat.chain.loader;

import java.io.InputStream;
import java.util.List;

/**
 * 加载资源
 **/

public interface ResourceLoader {


    String getContent(InputStream inputStream);


    List<String> getChunkList(String content);
}
