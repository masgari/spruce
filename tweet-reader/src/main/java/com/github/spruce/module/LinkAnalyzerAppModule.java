package com.github.spruce.module;

import com.beust.jcommander.Parameter;
import com.github.spruce.analyzer.LinkAnalyzer;
import com.github.spruce.analyzer.LinkAnalyzerImpl;
import com.github.spruce.cfg.Configs;
import com.github.spruce.client.HosebirdTweetReader;
import com.github.spruce.client.StatusesEndpointBuilder;
import com.github.spruce.client.TweetReader;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.twitter.Extractor;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;

import java.util.List;

import static com.github.spruce.client.HosebirdTweetReader.*;

/**
 * @author mamad
 * @since 29/10/14.
 */
public class LinkAnalyzerAppModule extends AbstractModule {
    private final Args args;

    public LinkAnalyzerAppModule(Args args) {
        this.args = args;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(ApiKey.class).to(args.appKey);
        bindConstant().annotatedWith(ApiSecret.class).to(args.appSecret);
        bindConstant().annotatedWith(AccessToken.class).to(args.accessToken);
        bindConstant().annotatedWith(AccessTokenSecret.class).to(args.accessTokenSecret);

        bind(LinkAnalyzer.class).to(LinkAnalyzerImpl.class);
        bind(TweetReader.class).to(HosebirdTweetReader.class);
        bind(Extractor.class).toInstance(new Extractor());
        bind(StreamingEndpoint.class).toInstance(StatusesEndpointBuilder.create().terms(args.terms).get());
    }

    public static class Args {
        @Parameter(description = "List of search terms", required = true)
        private List<String> terms = Lists.newArrayList();

        @Parameter(names = {"-h", "--help"}, description = "Display this help message.", help = true)
        private boolean help;

        @Parameter(names = {"-m", "--max-tweets"}, description = "Maximum number of tweets to read")
        private int maxTweets = Configs.spruce().maxTweets().asInt();

        @Parameter(names = {"-l", "--max-links"}, description = "Top frequent links to print")
        private int maxLinks = 10;

        @Parameter(names = {"-a", "--api"}, description = "Url of tweeter api")
        private String apiUrl = Configs.spruce().url().asString();

        @Parameter(names = {"-s", "--secret"}, password = true, description = "Spruce app api secret")
        private String appSecret = Configs.spruce().apiSecret().asString();

        @Parameter(names = {"-i", "--id"}, description = "Spruce app id")
        private String appKey = Configs.spruce().apiKey().asString();

        @Parameter(names = {"-t", "--token"}, description = "Tweeter access token")
        private String accessToken = Configs.spruce().accessToken().asString();

        @Parameter(names = {"-ts", "--token-secret"}, password = true, description = "Tweeter access token secret")
        private String accessTokenSecret = Configs.spruce().accessTokenSecret().asString();

        public boolean isHelp() {
            return help;
        }

        public List<String> getTerms() {
            return terms;
        }

        public int getMaxTweets() {
            return maxTweets;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public String getAppKey() {
            return appKey;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getAccessTokenSecret() {
            return accessTokenSecret;
        }

        public int getMaxLinks() {
            return maxLinks;
        }
    }
}
