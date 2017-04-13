package com.paralainer.telegram.bot.jekyll.composer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by stalov on 12/04/2017.
 */
public class BlogPostComposer {

    private Map<Integer, BlogPostPart> parts = new HashMap<>();

    public void clear() {
        parts.clear();
    }

    public void addText(Integer messageId, String text, Integer date) {
        parts.put(messageId, new TextPart(text, date));
    }

    public void editText(Integer messageId, String newText) {
        TextPart blogPostPart = (TextPart)parts.get(messageId);
        if (blogPostPart != null) {
            blogPostPart.setText(newText);
        }
    }

    public void addImage(Integer messageId, String url, Integer date) {
        parts.put(messageId, new ImagePart(url, date));
    }

    public String toMarkdown() {
        return parts.values().stream()
                .sorted(Comparator.comparing(BlogPostPart::getSendDate))
                .map(BlogPostPart::toMarkdown)
                .collect(Collectors.joining("\n\n"));
    }


    private static class TextPart extends BlogPostPart {
        private String text;

        private TextPart(String text, Integer date) {
            super(date);
            this.text = text;
        }

        public void setText(String text){
            this.text = text;
        }

        public String toMarkdown() {
            return text.trim();
        }
    }

    private static class ImagePart extends BlogPostPart {

        private String imageUrl;

        private ImagePart(String imageUrl, Integer date) {
            super(date);
            this.imageUrl = imageUrl;
        }

        public String toMarkdown() {
            return "![image](" + imageUrl + ")";
        }
    }

    private static abstract class BlogPostPart {

        private Integer sendDate;

        protected BlogPostPart(Integer sendDate) {
            this.sendDate = sendDate;
        }


        public abstract String toMarkdown();

        public Integer getSendDate() {
            return sendDate;
        }
    }

}
