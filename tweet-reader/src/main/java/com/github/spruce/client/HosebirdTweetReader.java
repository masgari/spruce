package com.github.spruce.client;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author mamad
 * @since 27/10/14.
 */
public class HosebirdTweetReader implements TweetReader {
    private final String apiKey;
    private final String apiSecret;
    private final String accessToken;
    private final String accessTokenSecret;
    private Client hosebirdClient;
    private final List<String> terms;
    /**
     * Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream
     */
    private final BlockingQueue<String> msgQueue = Queues.newLinkedBlockingQueue(100000);
    private final BlockingQueue<Event> eventQueue = Queues.newLinkedBlockingQueue(1000);

    public HosebirdTweetReader(String apiKey, String apiSecret, String accessToken, String accessTokenSecret,
                               Iterable<String> terms) {
        checkArgument(!isNullOrEmpty(apiSecret));
        checkArgument(!isNullOrEmpty(apiSecret));
        checkArgument(!isNullOrEmpty(accessToken));
        checkArgument(!isNullOrEmpty(accessTokenSecret));
        checkNotNull(terms);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.terms = ImmutableList.copyOf(terms);
    }

    @Override
    public void connect() {

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        // Optional: set up some followings and track terms
        hosebirdEndpoint.trackTerms(terms);
        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(apiKey, apiSecret, accessToken, accessTokenSecret);


        ClientBuilder builder = new ClientBuilder()
                // optional: mainly for the logs
                .name("Hosebird-Client-01")
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                        // optional: use this if you want to process client events
                .eventMessageQueue(eventQueue);

        hosebirdClient = builder.build();
        // Attempts to establish a connection.
        hosebirdClient.connect();
    }

    @Override
    public void stop() {
        if (hosebirdClient != null) {
            hosebirdClient.stop();
        }
    }

    @Override
    public long consume(Consumer<Tweet> consumer, long max) {
        checkNotNull(consumer, "Please provide a valid consumer.");
        checkNotNull(hosebirdClient, "Please connect first.");
        long count = 0;
        while (!hosebirdClient.isDone() && count < max) {
            try {
                String tweet = msgQueue.take();
                consumer.accept(new TweetImpl(tweet));
                count++;
            } catch (InterruptedException e) {
                throw Throwables.propagate(e);
            }
        }
        return count;
    }

}
