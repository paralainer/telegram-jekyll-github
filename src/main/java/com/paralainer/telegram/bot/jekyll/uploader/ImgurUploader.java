package com.paralainer.telegram.bot.jekyll.uploader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stalov on 13/04/2017.
 */
public class ImgurUploader implements ImageUploader {

    private String accessToken;

    public ImgurUploader(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String upload(String url) throws Exception{
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://api.imgur.com/3/image");
        post.setHeader(new BasicHeader("Authorization", "Bearer " + accessToken));
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("image", url));
        urlParameters.add(new BasicNameValuePair("type", "URL"));


        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(new InputStreamReader(response.getEntity().getContent())).getAsJsonObject();

        return result.getAsJsonObject("data").get("link").getAsString();
    }


}
