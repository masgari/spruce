package com.github.spruce.client;

/**
 * @author mamad
 * @since 27/10/14.
 */
public class TweetImpl implements Tweet {
    private final String json;

    public TweetImpl(String json) {
        this.json = json;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getText() {
        return json;
    }

    @Override
    public String getUser() {
        return null;
    }
}
