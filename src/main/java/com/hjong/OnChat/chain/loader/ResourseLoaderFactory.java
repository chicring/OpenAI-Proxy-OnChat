package com.hjong.OnChat.chain.loader;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static com.hjong.OnChat.chain.split.Consts.PDF_TYPE;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/

@Component
public class ResourseLoaderFactory {

    @Resource
    PdfFileLoader pdfFileLoader;


    public ResourceLoader getLoader(String type) {
        if (type.equals(PDF_TYPE)) {
           return pdfFileLoader;
        }
        return null;
    }
}
