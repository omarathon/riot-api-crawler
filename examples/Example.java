/*
    An examplar use of the Crawler.
    -------------------

    The outcome from this implementation of the Crawler is as follows:

    - (1) Only crawl Matches that are at least 20 minutes long,
    - (2) Only crawl Summoners that are at least level 30,
    - (3) Only process 5 recent Matches for each crawled Summoner,
    - (4) Format each Match into a set of statistics for both teams,
      and store the results in JSON files within a given directory.

    (1) is achieved by constructing a MatchFilter, utilising the preset GameDurationMatchFilter.
    (2) is achieved by constructing a SummonerFilter, utilising the preset LevelSummonerFilter.

    (1), (2) and (3) are then finalised by constructing a CrawlerConfig with the GameDurationMatchFilter and LevelSummonerFilter,
                     providing "5" as the argument for the maximum number of recent matches to obtain.

    (4) is achieved by constructing a MatchFormatter, utilising the preset TeamStatsMatchFormatter.

    (4) is then finalised by constructing an OutputHandler, utilising the preset FileOutputHandler,
        constructed with the TeamStatsMatchFormatter.

    -------------------
    After the implementing the Crawler, it shall be ran potentially indefinitely from an input summoner,
    where in this example such summoner is "pff" on EUW.
 */

import com.omarathon.riotapicrawler.presets.matchfilters.GameDurationMatchFilter;
import com.omarathon.riotapicrawler.presets.matchformatters.TeamStatsMatchFormatter;
import com.omarathon.riotapicrawler.presets.outputhandlers.FileOutputHandler;
import com.omarathon.riotapicrawler.presets.summonerfilters.LevelSummonerFilter;
import com.omarathon.riotapicrawler.src.Crawler;
import com.omarathon.riotapicrawler.src.lib.CrawlerConfig;
import net.rithms.riot.constant.Platform;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Example {
    /* THROWS:
       - IOException if the Logger failed to initialise when constructing the FileOutputHandler or Crawler,
     */
    public static void main(String[] args) throws IOException {
        // NOTE: To construct a Crawler, require an API key, OutputHandler, CrawlerConfig and Path object where the logs shall be generated.

        /* Firstly, we shall construct the Path object for the directory in which the logs shall be generated,
           where in this example it is in "C:/data/crawler".
           Note that the logs shall be in a file named "riotapicrawler-log.log".
         */
        Path directory = Paths.get("C:/data/crawler");

        /* Now we construct an OutputHandler, where in this example we use the preset FileOutputHandler,
           which shall write the Crawled matches to JSON files.

           The FileOutputHandler requires a MatchFormatter, which shall format the Match into an Object before storing it.
           In this example, we use the preset TeamStatsMatchFormatter, which obtains the statistics for both teams stored by the Riot API.
           It also requires a directory to generate the JSON files. We shall use the above directory at "C:/data/crawler".
           Note that three folders shall be generated from the FileOutputHandler in the input directory:
           - "fileoutputhandler-building" where the JSON files shall be built in,
           - "fileoutputhandler-results" where the JSON files shall be moved into after being fully built,
           - "fileoutputhandler-logs" where the logs from the FileOutputHandler shall be generated.
           */

        // Construct the MatchFormatter
        TeamStatsMatchFormatter formatter = new TeamStatsMatchFormatter();

        // Attempt to construct the FileOutputHandler. Shall throw an IOException if the Logger failed to initialise.
        FileOutputHandler outputHandler = new FileOutputHandler(formatter, directory);

        /* We proceed to construct the CrawlerConfig, the final element required before constructing the Crawler object.

           A CrawlerConfig requires a MatchFilter and a SummonerFilter to predicate the crawled upon Matches and Summoners.
           In this example, we shall use the preset GameDurationMatchFilter, which only allows Matches at least 20 minutes long,
           and the preset LevelSummonerFilter, which only allows Summoners at least level 30.

           It also requires a "maxMatches" parameter, which is the number of Matches to fetch for each crawled player and pass
           to the OutputHandler. In this example, we shall use 5, such that for every summoner we obtain their 5 recent Matches.
         */

        // Construct the MatchFilter
        GameDurationMatchFilter matchFilter = new GameDurationMatchFilter();

        // Construct the SummonerFilter
        LevelSummonerFilter summonerFilter = new LevelSummonerFilter();

        // Construct the CrawlerConfig, with 5 as the maxMatches parameter
        CrawlerConfig crawlerConfig = new CrawlerConfig(matchFilter, summonerFilter, 5);

        /* We are now in a position to construct the Crawler, utilising the above directory, outputHandler and crawlerConfig.
           One must replace the first parameter with their Riot API key, obtained from the Riot API website.

           This constructor may throw an IOException if the Logger failed to initialise.
         */
        Crawler crawler = new Crawler("YOUR API KEY HERE", outputHandler, crawlerConfig, directory);

        /* Proceed to run the crawler potentially indefinitely on a new Thread unless a dead end is reached or an error occurs.

           One must enter the base summoner to begin crawling from and their Platform.

           I have chosen my LoL summoner to begin crawling from.
         */
        crawler.run("pff", Platform.EUW);

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
