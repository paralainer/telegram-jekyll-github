package com.paralainer.telegram.bot.jekyll;

import com.paralainer.telegram.bot.jekyll.composer.BlogPostComposer;
import com.paralainer.telegram.bot.jekyll.publisher.Publisher;
import com.paralainer.telegram.bot.jekyll.uploader.ImageUploader;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Created by stalov on 12/04/2017.
 */
public class JekyllBot extends TelegramLongPollingBot {

    public static final String START_POST = "/start_post";
    public static final String END_POST = "/end_post";

    private BlogPostComposer composer = new BlogPostComposer();
    private ImageUploader uploader;
    private Publisher publisher;
    private Set<Integer> allowedUsers;
    private String blogUrl;
    private String botToken;
    private String botName;

    private BlogPost blogPost;

    public JekyllBot(String botToken, String botName, ImageUploader uploader, Publisher publisher, Set<Integer> allowedUsers, String blogUrl) {
        this.botToken = botToken;
        this.botName = botName;
        this.uploader = uploader;
        this.publisher = publisher;
        this.allowedUsers = allowedUsers;
        this.blogUrl = blogUrl;
        this.blogPost = new BlogPost();
    }

    public void onUpdateReceived(Update update) {
        if (update.hasEditedMessage() && allowedUsers.contains(update.getEditedMessage().getFrom().getId())){
            Message editedMessage = update.getEditedMessage();
            composer.editText(editedMessage.getMessageId(), editedMessage.getText());
            return;
        }

        Message message = update.getMessage();
        if (message == null || !allowedUsers.contains(message.getFrom().getId())) {
            return;
        }

        if (message.isCommand()) {
            switch (message.getText()) {
                case START_POST:
                    clear();
                    break;
                case END_POST:
                    blogPost.setText(composer.toMarkdown());
                    sendMessage(message.getChatId(), "Enter title:");
                    break;
            }
        } else if (message.isUserMessage()) {
            if (blogPost.getText() == null) {
                if (message.hasPhoto()) {
                    addPhoto(message);
                } else if (message.hasText()) {
                    composer.addText(message.getMessageId(), message.getText(), message.getDate());
                }
            } else if (message.hasText()){
                if (blogPost.getTitle() == null) {
                    blogPost.setTitle(message.getText());
                    sendMessage(message.getChatId(), "Enter url: ");
                } else if (blogPost.getUrl() == null) {
                    blogPost.setUrl(message.getText());
                    publish(message.getChatId());
                }

            }
        }
    }

    private void clear() {
        blogPost = new BlogPost();
        composer.clear();
    }

    private void addPhoto(Message message) {
        try {
            composer.addImage(message.getMessageId(), getPhotoUrl(message.getPhoto()), message.getDate());
        } catch (Exception e) {
            e.printStackTrace();

            sendMessage(message.getChatId(), "Error uploading photo: " + e.getLocalizedMessage());
        }
    }


    private String getPhotoUrl(List<PhotoSize> photoSizes) throws Exception {
        PhotoSize photoSize = photoSizes.stream().max(Comparator.comparing(PhotoSize::getWidth)).get();
        GetFile getFile = new GetFile();
        getFile.setFileId(photoSize.getFileId());
        File file = getFile(getFile);
        String fileUrl = file.getFileUrl(getBotToken());
        return uploader.upload(fileUrl);
    }

    private void publish(Long chatId) {
        try {
            sendMessage(chatId, "Publishing...");
            blogPost.setCategory("traveling");
            publisher.publish(blogPost);
            sendMessage(chatId, "Published");

            waitForPostIsAvailable(chatId, blogPost);

            clear();
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "Error: " + e.getLocalizedMessage());
        }
    }

    private void waitForPostIsAvailable(Long chatId, BlogPost blogPost) {
        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    String urlText = blogUrl + "/" + blogPost.getUrl() + "/";
                    URL url = new URL(urlText);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    if (urlConnection.getResponseCode() != 404) {
                        sendMessage(chatId, urlText);
                        break;
                    }

                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return botName;
    }

    public String getBotToken() {
        return botToken;
    }
}
