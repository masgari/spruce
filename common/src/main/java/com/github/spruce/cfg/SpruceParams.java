package com.github.spruce.cfg;

import com.google.common.base.Preconditions;

/**
 * @author Mamad Asgari
 * @since 22/09/2014
 */
public final class SpruceParams implements ConfigGroup {

    public static final String NAME = "spruce";

    public static final String URL = "api.url";
    public static final String API_KEY = "api.key";
    public static final String API_SECRET = "api.secret";
    public static final String MAX_TWEETS = "max.tweets";

    private final ConfigGroupImpl delegate;

    protected SpruceParams(ConfigGroupImpl delegate) {
        this.delegate = delegate;
        Preconditions.checkArgument(NAME.equals(delegate.name()));
        //register defaults
        delegate.defaultValue(URL, "https://api.twitter.com/1.1/");
        delegate.defaultValue(MAX_TWEETS, 1000);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Param entry(String key) {
        return delegate.entry(key);
    }

    public Param url() {
        return entry(URL);
    }

    public Param apiSecret() {
        return entry(API_SECRET);
    }

    public Param apiKey() {
        return entry(API_KEY);
    }

    public Param maxTweets() {
        return entry(MAX_TWEETS);
    }
}
