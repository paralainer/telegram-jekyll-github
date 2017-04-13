package com.paralainer.telegram.bot.jekyll;

import com.paralainer.telegram.bot.jekyll.publisher.GitJekyllPublisher;
import com.paralainer.telegram.bot.jekyll.uploader.ImgurUploader;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

import java.util.Arrays;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        checkRequiredVariables();
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(
                    new JekyllBot(
                            System.getenv("TELEGRAM_BOT_TOKEN"),
                            System.getenv("TELEGRAM_BOT_NAME"),
                            new ImgurUploader(System.getenv("IMGUR_ACCESS_TOKEN")),
                            new GitJekyllPublisher(System.getenv("BLOG_DIR"), ""),
                            Arrays.stream(System.getenv("ALLOWED_USERS").split(",")).map(x -> Integer.valueOf(x.trim())).collect(Collectors.toSet()),
                            System.getenv("BLOG_URL")
                    )
            );

            System.out.println("Bot is running");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkRequiredVariables() {
        String[] strings = {
                "TELEGRAM_BOT_TOKEN",
                "TELEGRAM_BOT_NAME",
                "IMGUR_ACCESS_TOKEN",
                "BLOG_DIR",
                "ALLOWED_USERS",
                "BLOG_URL"
        };

        boolean ok = true;
        for (String varName : strings) {
            if (System.getenv(varName) == null){
                ok = false;
                System.out.println(varName + " not set");
            }
        }

        if (!ok){
            System.exit(1);
        }
    }
}
