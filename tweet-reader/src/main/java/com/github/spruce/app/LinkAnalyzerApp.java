package com.github.spruce.app;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.spruce.analyzer.LinkAnalyzer;
import com.github.spruce.cfg.Configs;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author mamad
 * @since 27/10/14.
 */
public class LinkAnalyzerApp {
    public static void main(String[] arguments) {
        Args args = new Args();
        JCommander parser = new JCommander(args);
        try {
            parser.parse(arguments);
            if (args.isHelp()) {
                parser.usage();
                return;
            }
        } catch (Exception e) {
            parser.usage();
            return;
        }

        LinkAnalyzer linkAnalyzer = new LinkAnalyzer();
    }

    private static class Args {
        @Parameter(description = "List of tweeter account to analyze links in their tweets", required = true)
        private List<String> accounts = Lists.newArrayList();

        @Parameter(names = {"-h", "--help"}, description = "Display this help message.", help = true)
        private boolean help;

        @Parameter(names = {"-m", "--max-tweets"}, description = "Maximum number of tweets to read")
        private int maxTweets = Configs.spruce().maxTweets().asInt();

        @Parameter(names = {"-a", "--api"}, description = "Url of tweeter api")
        private String apiUrl = Configs.spruce().url().asString();

        @Parameter(names = {"-s", "--secret"}, password = true, description = "Spruce app api secret")
        private String appSecret = Configs.spruce().apiSecret().asString();

        @Parameter(names = {"-i", "--id"}, description = "Spruce app id")
        private String appKey = Configs.spruce().apiKey().asString();

        public boolean isHelp() {
            return help;
        }

        public List<String> getAccounts() {
            return accounts;
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
    }
}
