package com.hjong.OnChat.chain.split;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static com.hjong.OnChat.chain.split.Consts.PDF_TYPE;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/

@Component
public class SpliterFactory {

    @Resource
    PdfSplitter pdfSplitter;

    public Splitter getSplitter(String type) {
        if (type.equals(PDF_TYPE)) {
            return pdfSplitter;
        }
        return null;
    }
}
