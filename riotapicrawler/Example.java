package com.omarathon.riotapicrawler;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Platform;
import com.omarathon.riotapicrawler.presets.matchfilters.GameDurationMatchFilter;
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

public class Example {
    public static void main(String[] args) {
        // NOTE: To construct a Crawler, require an API key, OutputHandler and a CrawlerConfig.

        // Construct the MatchFormatter, which is a TeamStatsMatchFormatter
        MatchFormatter matchFormatter = new TeamStatsMatchFormatter();

        // Construct the OutputHandler, which is a PrintOutputHandler which uses a StringFormatter that shall format the input Matches into their team stats.
        OutputHandler outputHandler = new PrintOutputHandler(new StringMatchFormatter(matchFormatter));

        // Construct the MatchFilter, which is a GameDurationMatchFilter that shall only allow Matches that are 20 mins or longer.
        MatchFilter matchFilter = new GameDurationMatchFilter();

        // Construct the SummonerFilter, which is a LevelSummonerFilter that shall only allow Summoners level 30+.
        SummonerFilter summonerFilter = new LevelSummonerFilter();

        // Construct the CrawlerConfig, with 5 as the maxMatches parameter, which uses our established filters.
        CrawlerConfig crawlerConfig = new CrawlerConfig(matchFilter, summonerFilter, 5);

        // Set the API Key for Orianna
        Orianna.setRiotAPIKey("YOUR API KEY HERE");

        // Construct the Crawler with our CrawlerConfig and OutputHandler. We do not pass our own CrawlerListener instance, so that we use the default System.out printing listener.
        Crawler crawler = new Crawler(crawlerConfig, outputHandler);

        /* Proceed to run the crawler potentially indefinitely on a new Thread unless a dead end is reached or an error occurs.

           One must enter the base summoner to begin crawling from and their Platform.

           I have chosen my LoL summoner to begin crawling from.
         */
        crawler.run("pff", Platform.EUROPE_WEST);

        /* For further example, since the above crawler.run() is executing on a new Thread,
           we may stop the crawler after some x milliseconds my sleeping for such time.
           After the sleep, one may call stop() in the Crawler object, which shall gracefully end the operation of the crawler.

           In this example, we shall stop the crawler after 30 seconds, which is 30 * 1000 milliseconds.
         */
        try {
            Thread.sleep(30 * 1000);
        }
        catch (InterruptedException e) { // Thread was interrupted while sleeping, so end the crawler prematurely by skipping to stop() outside of the try.
        }
        // Stop the crawler
        crawler.stop();
    }
}
