package com.hjong.OnChat.chain.loader;

import com.hjong.OnChat.chain.split.PdfSplitter;
import com.hjong.OnChat.chain.split.Splitter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/
@Slf4j
@Component
public class PdfFileLoader implements ResourceLoader{

    @Resource
    Splitter pdfSplitter;

    @Override
    public String getContent(InputStream inputStream) {
        try {
            PDDocument document = PDDocument.load(inputStream);
            try {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            } finally {
                document.close();
            }
        } catch (IOException e) {
            log.error("Failed to load pdf file", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getChunkList(String content) {
        return pdfSplitter.split(content);
    }
}
