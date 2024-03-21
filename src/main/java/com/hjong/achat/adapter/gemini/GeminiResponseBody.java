package com.hjong.achat.adapter.gemini;

import lombok.Data;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Data
public class GeminiResponseBody {

    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Integer index;
        @Data
        public static class Content {
            private List<Part> parts;
            private String role;

            @Data
            public static class Part {
                private String text;
            }
        }
    }
}