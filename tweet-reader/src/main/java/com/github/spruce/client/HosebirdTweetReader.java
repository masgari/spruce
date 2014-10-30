package com.github.spruce.client;

import com.google.common.base.Throwables;
import com.google.common.collect.Queues;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author mamad
 * @since 27/10/14.
 */
@Singleton
public class HosebirdTweetReader implements TweetReader {
    private static final Logger LOG = LoggerFactory.getLogger(HosebirdTweetReader.class);

    private final String apiKey;
    private final String apiSecret;
    private final String accessToken;
    private final String accessTokenSecret;
    private final StreamingEndpoint endpoint;
    /**
     * Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream
     */
    private final BlockingQueue<String> msgQueue = Queues.newLinkedBlockingQueue(100000);
    private final BlockingQueue<Event> eventQueue = Queues.newLinkedBlockingQueue(1000);
    private Client hosebirdClient;

    @Inject
    public HosebirdTweetReader(@ApiKey String apiKey, @ApiSecret String apiSecret,
                               @AccessToken String accessToken, @AccessTokenSecret String accessTokenSecret,
                               StreamingEndpoint endpoint) {
        checkArgument(!isNullOrEmpty(apiSecret));
        checkArgument(!isNullOrEmpty(apiSecret));
        checkArgument(!isNullOrEmpty(accessToken));
        checkArgument(!isNullOrEmpty(accessTokenSecret));
        checkNotNull(endpoint);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.endpoint = endpoint;
    }

    @Override
    public void connect() {

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        Authentication hosebirdAuth = new OAuth1(apiKey, apiSecret, accessToken, accessTokenSecret);

        ClientBuilder builder = new ClientBuilder()
                // optional: mainly for the logs
                .name("Hosebird-Client-01")
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                        // optional: use this if you want to process client events
                .eventMessageQueue(eventQueue);

        hosebirdClient = builder.build();
        // Attempts to establish a connection.
        hosebirdClient.connect();
        LOG.debug("HoseBird client connected.");
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
        checkNotNull(hosebirdClient, "Please call connect() first.");
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
        LOG.debug("Total tweets:{}", count);
        return count;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface ApiKey {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface ApiSecret {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface AccessToken {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface AccessTokenSecret {
    }
}
