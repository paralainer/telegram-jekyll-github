package com.paralainer.telegram.bot.jekyll.publisher;

import com.paralainer.telegram.bot.jekyll.BlogPost;

/**
 * Created by stalov on 13/04/2017.
 */
public interface Publisher {

    void publish(BlogPost post);
}
