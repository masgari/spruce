package com.github.spruce.client;

import java.util.function.Consumer;

/**
 * @author mamad
 * @since 27/10/14.
 */
public interface TweetReader {
    void connect();
    long consume(Consumer<Tweet> consumer, long max);
    void stop();
}
