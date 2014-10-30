package com.github.spruce.analyzer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.List;
import java.util.function.BiConsumer;

import static com.google.common.collect.Multisets.copyHighestCountFirst;

/**
 * @author mamad
 * @since 29/10/14.
 */
public class LinkAnalyzingResults {
    private final Multiset<String> stats = HashMultiset.create();
    private long totalTweets;

    public void addUrls(List<String> urls) {
        stats.addAll(urls);
    }

    public LinkAnalyzingResults consume(final int maxLinks, final BiConsumer<String, Integer> consumer) {
        copyHighestCountFirst(stats).entrySet()
                .stream()
                .limit(maxLinks)
                .forEach(entry -> consumer.accept(entry.getElement(), entry.getCount()));
        return this;
    }

    public boolean hasAnyLink() {
        return !stats.isEmpty();
    }

    public long getTotalTweets() {
        return totalTweets;
    }

    public void setTotalTweets(long totalTweets) {
        this.totalTweets = totalTweets;
    }
}
