package com.github.spruce.client;

import com.twitter.Extractor;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.github.spruce.cfg.Configs.spruce;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author mamad
 * @since 27/10/14.
 */
public class HosebirdTweetReaderIntegTest {
    @Before
    public void setUp() throws Exception {
        Assume.assumeNotNull(spruce().apiSecret());
        Assume.assumeNotNull(spruce().accessTokenSecret());
    }

    @Test
    public void testReadSomeTweets() throws Exception {
        HosebirdTweetReader reader = new HosebirdTweetReader(spruce().apiKey().asString(),
                spruce().apiSecret().asString(),
                spruce().accessToken().asString(),
                spruce().accessTokenSecret().asString(),
                StatusesEndpointBuilder.create().terms("job", "movie").get());
        long count = 10;
        Extractor extractor = new Extractor();
        try {
            reader.connect();
            count = reader.consume(tweet -> {
                assertNotNull(tweet);
                String tweetText = tweet.getText();
                List<String> urls = extractor.extractURLs(tweetText);
                if (!urls.isEmpty()) {
                    System.out.println("urls = " + urls);
                    System.out.println("tweet = " + tweetText);
                }
            }, 5);
        } finally {
            reader.stop();
        }

        assertTrue(count > 0);
    }
}
