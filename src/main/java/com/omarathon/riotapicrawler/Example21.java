/*
    In this example, we only crawl Matches that are 20 minutes or longer, and only crawl Summoners that are level 30+.
    We print its results to System.out, but we format the Matches into a Teams object before printing them.
    (The Teams object stores the Team object for the red and blue teams, where a Team object stores game statistics for the team).

    We run the crawler for 30 seconds, by calling stop() after 30 seconds.
 */

package com.omarathon.riotapicrawler;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Platform;
import com.omarathon.riotapicrawler.presets.crawlerconfigs.BasicCrawlerConfig;
import com.omarathon.riotapicrawler.presets.matchfilters.GameDurationMatchFilter;
import com.omarathon.riotapicrawler.presets.matchformatters.DoNothingMatchFormatter;
import com.omarathon.riotapicrawler.presets.matchformatters.StringMatchFormatter;
import com.omarathon.riotapicrawler.presets.matchformatters.TeamStatsMatchFormatter;
import com.omarathon.riotapicrawler.presets.outputhandlers.PrintOutputHandler;
import com.omarathon.riotapicrawler.presets.summonerfilters.LevelSummonerFilter;
import com.omarathon.riotapicrawler.src.Crawler;
import com.omarathon.riotapicrawler.src.lib.CrawlerConfig;
import com.omarathon.riotapicrawler.src.lib.filter.MatchFilter;
import com.omarathon.riotapicrawler.src.lib.filter.SummonerFilter;
import com.omarathon.riotapicrawler.src.lib.formatter.MatchFormatter;
import com.omarathon.riotapicrawler.src.lib.handler.OutputHandler;

public class Example21 {
    public static void main(String[] args) {
        // Construct the PrintOutputHandler with a StringFormatter
        // that uses a DoNothingMatchFilter, so we format the Matches
        // into unmodified Strings and print them to System.out.
        OutputHandler outputHandler  = new PrintOutputHandler(new StringMatchFormatter(new DoNothingMatchFormatter()));

        // Construct a BasicCrawlerConfig, with 5 as the input maxMatches parameter
        // (number of recent Matches to obtain from each match history)
        CrawlerConfig crawlerConfig = new BasicCrawlerConfig(5);

        // Construct a Crawler, with the above CrawlerConfig and OutputHandler
        Crawler crawler = new Crawler(crawlerConfig, outputHandler);

        // Set Orianna's API key to your Riot API key
        Orianna.setRiotAPIKey("YOUR RIOT API KEY GOES HERE");

        // Run the Crawler on a new thread, starting from an input Summoner of choice
        // (with their Platform). Chosen here is https://euw.op.gg/summoner/userName=pff.
        crawler.run("pff", Platform.EUROPE_WEST);
    }
}
