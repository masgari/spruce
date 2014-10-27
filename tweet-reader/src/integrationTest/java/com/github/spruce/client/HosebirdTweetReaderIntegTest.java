package com.github.spruce.client;

import com.google.common.collect.Lists;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static com.github.spruce.cfg.Configs.spruce;
import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 27/10/14.
 */
public class HosebirdTweetReaderIntegTest {
    @Before
    public void setUp() throws Exception {
        Assume.assumeNotNull(spruce().apiSecret());
    }

    @Test
    public void testReadSomeTweets() throws Exception {
        HosebirdTweetReader reader = new HosebirdTweetReader(spruce().apiKey().asString(),
                spruce().apiSecret().asString(),
                spruce().accessToken().asString(),
                spruce().accessTokenSecret().asString(),
                Lists.newArrayList("Android", "TV"));
        long count = 0;
        try {
            reader.connect();
            count = reader.consume(tweet -> {
                assertNotNull(tweet);
                System.out.println("tweet = " + ((TweetImpl) tweet).getJson());
            }, 5);
        } finally {
            reader.stop();
        }

        assertTrue(count > 0);
    }
}
