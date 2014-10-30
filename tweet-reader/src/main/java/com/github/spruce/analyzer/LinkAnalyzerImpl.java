package com.github.spruce.analyzer;

import com.github.spruce.client.TweetReader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twitter.Extractor;

import java.util.List;

/**
 * @author mamad
 * @since 27/10/14.
 */
@Singleton
public class LinkAnalyzerImpl implements LinkAnalyzer {
    private final TweetReader tweetReader;
    private final Extractor extractor;

    @Inject
    public LinkAnalyzerImpl(TweetReader tweetReader, Extractor extractor) {
        this.tweetReader = tweetReader;
        this.extractor = extractor;
    }

    @Override
    public LinkAnalyzingResults analyze(int maxTweets) {
        LinkAnalyzingResults results = new LinkAnalyzingResults();
        long count = tweetReader.consume(tweet -> {
            List<String> urls = extractor.extractURLs(tweet.getText());
            results.addUrls(urls);
        }, maxTweets);

        results.setTotalTweets(count);
        return results;
    }
}
