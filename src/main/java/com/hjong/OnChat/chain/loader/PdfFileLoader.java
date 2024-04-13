package com.hjong.OnChat.chain.loader;

import com.hjong.OnChat.chain.split.PdfSplitter;
import com.hjong.OnChat.chain.split.Splitter;
import jakarta.annotation.Resource;
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
@Component
public class PdfFileLoader implements ResourceLoader{

    @Resource
    Splitter pdfSplitter;

    @Override
    public String getContent(InputStream inputStream) {
        try {
            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getChunkList(String content) {
        return pdfSplitter.split(content);
    }
}
