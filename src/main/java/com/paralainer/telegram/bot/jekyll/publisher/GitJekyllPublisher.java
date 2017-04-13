package com.paralainer.telegram.bot.jekyll.publisher;

import com.paralainer.telegram.bot.jekyll.BlogPost;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by stalov on 13/04/2017.
 */
public class GitJekyllPublisher implements Publisher {

    private String postsDir;
    private String templateFile;

    public static final String TEMPLATE =
                    "---\n" +
                    "layout: post\n" +
                    "category: $category\n" +
                    "title: $title\n" +
                    "---\n\n" +
                    "$text";


    public GitJekyllPublisher(String postsDir, String templateFile) {
        this.postsDir = postsDir;
        this.templateFile = templateFile;
    }


    @Override
    public void publish(BlogPost blogPost) {
        String text = TemplateFormatter.templateToString(TEMPLATE, blogPost);

        //System.out.println(text);

        putToRepository(blogPost, text);
    }

    private void putToRepository(BlogPost blogPost, String text) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try {
            String fileName = date + "-" + blogPost.getUrl() + ".md";
            Path file = Files.createFile(Paths.get(postsDir, "_posts", blogPost.getCategory(), fileName));
            Files.write(file, Collections.singletonList(text));

            runCommand("git add --all");
            runCommand("git pull");
            runCommand("git commit -m " + blogPost.getUrl());
            runCommand("git push");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runCommand(String command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command.split(" +"));
        pb.directory(new File(postsDir));
        pb.redirectErrorStream(true);

        Process process = pb.start();
        System.out.println(new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining()));
    }
}
