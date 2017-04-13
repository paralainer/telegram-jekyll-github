package com.paralainer.telegram.bot.jekyll.publisher;

import com.paralainer.telegram.bot.jekyll.BlogPost;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stalov on 13/04/2017.
 */
public class TemplateFormatter {


    public static String templateToString(String template, BlogPost blogPost){
        Map<String,String> tokens = new HashMap<>();
        tokens.put("\\$category", blogPost.getCategory());
        tokens.put("\\$title", blogPost.getTitle());
        tokens.put("\\$text", blogPost.getText());

        String patternString = "(" + String.join("|",tokens.keySet()) + ")";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(template);

        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb, tokens.get(matcher.group(1).replace("$", "\\$")));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

}
