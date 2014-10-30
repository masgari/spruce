package com.github.spruce.app;

import com.beust.jcommander.JCommander;
import com.github.spruce.analyzer.LinkAnalyzer;
import com.github.spruce.analyzer.LinkAnalyzingResults;
import com.github.spruce.client.TweetReader;
import com.github.spruce.module.LinkAnalyzerAppModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import static com.github.spruce.module.LinkAnalyzerAppModule.Args;

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
        Injector injector = Guice.createInjector(new LinkAnalyzerAppModule(args));
        TweetReader tweetReader = injector.getInstance(TweetReader.class);
        try {
            tweetReader.connect();

            LinkAnalyzer linkAnalyzer = injector.getInstance(LinkAnalyzer.class);
            LinkAnalyzingResults results = linkAnalyzer.analyze(args.getMaxTweets());
            if (results.hasAnyLink()) {
                System.out.printf("%-50s\t%-10s%n", "URL", "Count");
                System.out.printf("%-50s\t%-10s%n", "----------------------------------------", "--------");
                results.consume(args.getMaxLinks(), (url, count) -> {
                    System.out.printf("%-50s\t%,d%n", url, count);
                });
            } else {
                System.out.printf("No links found in the %,d tweets.", results.getTotalTweets());
            }
        } finally {
            tweetReader.stop();
        }
    }
}
