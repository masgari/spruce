package com.github.spruce.analyzer;

import com.github.spruce.client.TweetReader;

/**
 * @author mamad
 * @since 27/10/14.
 */
public class LinkAnalyzer {
    private final TweetReader tweetReader;

    public LinkAnalyzer(TweetReader tweetReader) {
        this.tweetReader = tweetReader;
    }
}
