/*
    A League of Legends match crawler, utilising taycaldwell's Riot Games API Java Library.
    (Ref: https://github.com/taycaldwell/riot-api-java)

    Functionality:
    Provide your Riot API key, as well as an OutputHandler, CrawlerConfig and Path where the logs shall be generated.
    - Note: one may use preset OutputHandlers and CrawlerConfigs provided.

    After constructing a Crawler object, one calls run with an input summoner, which shall crawl on a separate Thread indefinitely from the input summoner
    until either a dead end or stop() was called asynchronously.
    Logs detailing the operation shall be generated at the input log directory, in a file at path: directory/riotapicrawler-log.log

    Crawling a player:
    - fetch their match history from the Riot API, with the number of recent games specified in the crawler config.
    - pass each match in their recent match history to the output handler which shall process each Match object.
    - iterate over each Match, determining which Matches are crawlable by the crawler config's match filter.
    - On the crawlable Matches, fetch the players in that game from the Riot API.
    - Repeatedly pick a random player from the Match until the player is crawlable, as specified in the crawler config's summoner filter,
      and if the player has not already been crawled.
    - if found, go to step 1 with this new player.
    - otherwise, we have iterates over all matches and found no crawlable players in each, therefore we have reached a dead end, STOP CRAWLING.

    Author: Omar Tanner.
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.src;

import com.omarathon.riotapicrawler.src.lib.*;
import javafx.util.Pair;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.ApiMethod;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.match.dto.*;
import net.rithms.riot.api.endpoints.match.methods.GetMatch;
import net.rithms.riot.api.endpoints.match.methods.GetMatchListByAccountId;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.api.endpoints.summoner.methods.GetSummoner;
import net.rithms.riot.api.endpoints.summoner.methods.GetSummonerByName;
import net.rithms.riot.api.request.ratelimit.*;
import net.rithms.riot.constant.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Crawler {
    /* Storing the summoners the crawler has visited in this instance,
       such that duplicate Matches are not obtained and crawled. */
    private HashSet<String> visitedSummoners;
    // The ApiConfig for the instance of the RiotApi object
    private ApiConfig apiConfig;
    // The instance of the api from which we can make requests using the API key passed to the constructor
    private RiotApi api;
    /* A configuration for the crawler, which shall determine which matches and players are crawlable,
       as well as the maximum number of matches to obtain from a crawlable player's recent match history. */
    private CrawlerConfig crawlerConfig;
    // The handler for each outputted Match object, which shall do something with each obtained Match.
    private OutputHandler outputHandler;
    /* A boolean flag determining whether the crawler is actively crawling or not.
      One cannot terminate the crawler if this flag is true. */
    private boolean crawling;
    // The Logger object which shall be used to send log messages to
    private Logger log;

    /*
       INPUTS:
       apiKey - Riot API key for the crawler to use.
       outputHandler - the handler for each outputted Match object, which shall do something with each obtained Match.
       crawlerConfig - a configuration for the crawler, which shall determine which matches and players are crawlable,
                       as well as the maximum number of matches to obtain from a crawlable player's recent match history.
       logDirectory - the Path object to the directory in which logs may be generated for the crawler.

       THROWS:
       IOException -  when the logger failed to be configured.
    */
    public Crawler(String apiKey, OutputHandler outputHandler, CrawlerConfig crawlerConfig, Path logDirectory) throws IOException {
        // Construct an ApiConfig from the input apiKey, then construct the RiotApi object for the crawler from such config.
        apiConfig = new ApiConfig().setKey(apiKey);
        apiConfig.setRateLimitHandler(new DefaultRateLimitHandler());
        this.api = new RiotApi(apiConfig);

        // Set the output handler and crawler config to the input ones
        this.outputHandler = outputHandler;
        this.crawlerConfig = crawlerConfig;

        // Initialise visitedSummoners as an empty HashSet
        visitedSummoners = new HashSet<String>();

        // Construct input log directory if it doesn't already exist
        File dir = logDirectory.toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            // If fail to construct the directories mkdirs shall return false, in which case throw RuntimeError
            if (!dir.mkdirs()) throw new RuntimeException("Failed to make the log directory!");
        }

        // Initialise the log as the given logDirectory
        this.log = Logger.getLogger("RiotAPICrawlerLog");
        // Configure the logger with handler and formatter
        FileHandler fh = new FileHandler(logDirectory + "/riotapicrawler-log.log");
        this.log.addHandler(fh);

        // Initialise crawling to false (have not run yet)
        this.crawling = false;
    }

    // Opens a new Thread, and starts crawling from an input summoner name, where such summoner is on the given Platform (e.g EUW)
    // If cannot obtain Summoner object for input summonerName from api (RiotApiException) then terminate crawling operation.
    public void run(String summonerName, Platform platform) {
        log.info("BEGAN CRAWLING - Input Summoner Name: " + summonerName + ", Platform: " + platform.toString());
        try {
            // Obtain input base summoner
            Summoner summoner = (Summoner) runApiMethodWithThrottle(new GetSummonerByName(apiConfig, platform, summonerName));
            // Obtain summoner filter from crawler config to determine if input base summoner is crawlable
            if (crawlerConfig.getSummonerFilter().filter(summoner, platform)) { // Crawlable
                // Open a new thread and begin a crawl from the input player on such thread.
                Thread crawlThread = new Thread(() -> {
                    crawl(summoner, platform);
                });
                crawlThread.start();
            }
            else { // Not crawlable
                log.severe("[ERROR} STOPPED CRAWLING - input summoner isn't crawlable.");
            }
        }
        catch (RiotApiException e) { // Failed to obtain, thus end crawling operation.
            log.severe("[FATAL ERROR] Failed to obtain base Summoner to initiate crawl! Summoner name: " + summonerName + ", Platform: " + platform.toString() + ". Proceeding to log error string and stack trace!");
            log.severe("[ERROR STRING] " + e.toString());
            log.severe("[STACK TRACE] " + e.getStackTrace().toString());
        }
    }

    /* Main crawling loop function that crawls on the Summoner given by summoner, on Platform given by platform.

       ERROR Handling: - For all API calls, if a RateLimitException occurs it is handled by throttling the request.
                       - When obtaining MatchList for the summoner being crawled, all errors except a RateLimitException cause the crawler to stop crawling.
                       - When obtaining the Match object for each MatchReference in the list of MatchReferences, any RiotApiException shall mean such Match is not sent to output handler nor is stored in potentially crawled Matches ArrayList
                       - When obtaining the Summoner object for each Player in a Match, any RiotApiException shall mean that such Summoner will not be crawled, and is skipped.
     */
    private void crawl(Summoner summoner, Platform platform) {
        // Began crawling, so set crawling flag to true.
        crawling = true;
        log.severe("[INITIAL CRAWL] Began crawl on Summoner Account ID: " + summoner.getAccountId() + " on Platform: " + platform.toString());
        // Potentially indefinite polling loop, exit when crawling is false - may be set false externally or internally.
        while (crawling) {
            // Prefix for the log messages to identify this specific crawl.
            String logCrawlString = "[CRAWL " + summoner.getAccountId() + "]";
            /* Obtain the list of matches for the input summoner,
              with the endIndex parameter set to the crawler configuration's max matches parameter.
              beginIndex in the API begins at 0 if not set, therefore we obtain a maximum of maxMatches matches.
              See documentation of riot-api-java for reference (or the official LoL API documentation) */
            MatchList matchList;
            try {
                matchList = (MatchList) runApiMethodWithThrottle(new GetMatchListByAccountId(apiConfig, platform, summoner.getAccountId(), null, null, null, -1, -1, -1, crawlerConfig.getMaxMatches()));
            }
            catch (RiotApiException e) { // Error obtaining the MatchList for the summoner, therefore we cannot crawl them, stop the operation and log the error.
                log.severe(logCrawlString + " [FATAL ERROR] Failed to obtain MatchList for summoner! Logging error string and stack trace!");
                log.severe(logCrawlString + " [ERROR STRING] " + e.toString());
                log.severe(logCrawlString + " [STACK TRACE] " + e.getStackTrace().toString());
                log.severe("ENDING CRAWL OPERATION DUE TO FAILURE IN OBTAINING MATCH LIST");
                // Stop crawling so set crawling flag to false and exit crawling loop
                crawling = false;
                break;
            }
            log.info(logCrawlString + " Successfully obtained match list!");
            // Add the currently crawled summoner to the visited summoners HashSet
            visitedSummoners.add(summoner.getAccountId());
            // Obtain MatchReference objects to iterate over
            List<MatchReference> matchRefs = matchList.getMatches();
            if (matchRefs != null) { // Checking we have MatchReferences to iterate
                log.info(logCrawlString + " Found MatchReferences to iterate!");
                // New ArrayList to store the Match objects for each MatchReference
                ArrayList<Match> matches = new ArrayList<Match>();
                // Store all obtained matches in above matches ArrayList and send them all to the output handler
                processMatches(matchRefs, platform, matches, logCrawlString);

                /* Now iterate over each obtained Match object, picking a random summoner from the list of summoners for that game.
                   If the picked summoner is crawlable (testable via the crawler config's summoner filter) then change the original
                   parameters to this method to the new summoner, set found to true to break out of the search loops
                   and then the while loop shall perform a crawl on them.
                   Otherwise, pick another random summoner and repeat, until no more summoners.
                   If no more summoners, the found flag remains false, and if so we stop crawling by setting crawling to false.
                   (Since we are at a dead end). */
                boolean found = false;
                log.info(logCrawlString + " Beginning search for crawlable player from the match history!");
                // Now obtain the next summoner to crawl
                Pair<Summoner, Platform> nextCrawl = seekNextCrawl(matches, logCrawlString);

                // If nextCrawl is not null then we found a summoner to crawl so set this as the summoner for the next iteration
                if (nextCrawl != null) {
                    summoner = nextCrawl.getKey();
                    platform = nextCrawl.getValue();
                }
                else { // is null therefore no summoner found to crawl - dead end
                    crawling = false;
                    log.severe(logCrawlString + " Reached dead end (no crawlable player found), stopping crawl!");
                }
            }
            else { // the list of MatchReferences is null therefore no MatchReference to iterate...
                crawling = false;
                log.severe(logCrawlString + " No MatchReferences to iterate, stopping crawl!");
            }
            // Now we shall continue iterating if crawling is still true.
        }
        // Reached dead end or prematurely stopped crawling (by calling stop()).
        log.warning("[SAFE TERMINATION] STOPPED CRAWLING - Either reached dead end or prematurely stopped crawling by a stop() call.");
    }

    /* Takes a list of MatchReferences, iterates through them and obtains the corresponding Match object from the RiotApi.
       Adds the Match object to the input ArrayList to populate, and sends the Match object to the output handler.
       If a RiotApiException occurs that is not a RateLimitException, do not add the Match object to the ArrayList, neither pass it to the output handler.
    */
    private void processMatches(List<MatchReference> matchRefs, Platform platform, ArrayList<Match> matches, String logCrawlString) {
        for (MatchReference matchRef : matchRefs) {
            long gameId = matchRef.getGameId();
            // Use the RiotApi instance to obtain the Match object via the gameId and platform
            try {
                Match match = (Match) runApiMethodWithThrottle(new GetMatch(apiConfig, platform, gameId, null));
                // Add the obtained Match object to the ArrayList storing them
                matches.add(match);
                log.info(logCrawlString + " Obtained Match object for Game ID " + gameId + " - passing to output handler!");
                // Now allow the output handler to do further action with this Match
                outputHandler.handle(match);
            }
            catch (RiotApiException e) {
                log.warning(logCrawlString + " [ERROR] Failed to obtain Match object for Game ID " + gameId + " - shall not crawl or send to handler! Proceeding to log error string and stack trace!");
                log.warning(logCrawlString + " [ERROR STRING] " + e.toString());
                log.warning(logCrawlString + " [STACK TRACE] " + e.getStackTrace().toString());
            }
        }
    }

    /* Obtains the next Summoner object to crawl from a given ArrayList of Match objects.
       Returns a Pair of the Summoner and their Platform. since both information are required.

       Iterates through each Match object, and passes the Match object through the MatchFilter within the crawler config
       to determine whether such Match is crawlable. If not, move to next Match, otherwise obtain the Participants in the Match.
       Then for each Participant, obtain the corresponding Summoner via the RiotApi, and if a RiotApiException occurred that is not
       a RateLimitException then shall skip this participant.
       After obtaining the Summoner, pass it through the SummonerFilter within the crawler config to determine if crawlable.
       If not, move to next player, but if so, return a Pair of such Summoner with their Platform - we have found the summoner.

       If we exit the loop over the Matches without returning anything, we have scanned all Matches and found no crawlable player.
       Thus, return null to indicate no found Summoner.
    */
    private Pair<Summoner, Platform> seekNextCrawl(ArrayList<Match> matches, String logCrawlString) {
        log.info(logCrawlString + " Beginning search for crawlable player from the match history!");
        // Now iterate over each Match object in the ArrayList of Matches
        for (Match match : matches) {
            // Check if the Match is crawlable via the crawler config
            if (crawlerConfig.getMatchFilter().filter(match)) { // Is crawlable
                log.info(logCrawlString + " Crawlable match found, picking random summoners from participants to find crawlable summoner!");
                // Obtain the List of ParticipantIdentities for each player in the Match
                List<ParticipantIdentity> participants = match.getParticipantIdentities();
                // Now we enter the random summoner picking loop. Iterate while list if participants is not empty (so we can pick at least one)
                while (!participants.isEmpty()) {
                    // Obtain random ParticipantIdentity from list
                    ParticipantIdentity participant = participants.get(new Random().nextInt(participants.size()));
                    // Obtain Player object of the Participant
                    Player player = participant.getPlayer();
                    // Obtain Platform of the Player and the Summoner object for the Player from the RiotApi instance
                    Platform platformNew = Platform.getPlatformById(player.getPlatformId());
                    Summoner summonerNew;
                    try {
                        summonerNew = (Summoner) runApiMethodWithThrottle(new GetSummoner(apiConfig, platformNew, player.getSummonerId()));
                    }
                    catch (RiotApiException e) { // Error obtaining Summoner object for the Player, so reduce size of participant list so that on next iteration shall pick from the reduced selection
                        log.warning(logCrawlString + " [ERROR] Failed to obtain Summoner object for the next summoner to crawl from Account ID " + player.getAccountId() + "! Proceeding to log error string and stack trace! Skipping summoner!");
                        log.warning(logCrawlString + " [ERROR STRING] " + e.toString());
                        log.warning(logCrawlString + " [STACK TRACE] " + e.getStackTrace().toString());
                        participants.remove(participant);
                        continue;
                    }
                    // Now check that the summoner is crawlable via the crawler config, and that they're not already visited. If both are true, we may move the crawler to this new summoner.
                    if (crawlerConfig.getSummonerFilter().filter(summonerNew, platformNew) && !visitedSummoners.contains(summonerNew.getId())) {
                        log.info(logCrawlString + " Crawlable player found! Breaking out of current crawl to initiate crawl on them.");
                        // Shall crawl summonerNew, so create Pair and return
                        return new Pair<Summoner, Platform>(summonerNew, platformNew);
                    }
                    else { // Summmoner not crawlable, so reduce size of participant list so that on next iteration shall pick from the reduced selection
                        participants.remove(participant);
                    }
                }
            }
        }
        // Reach here means no crawlable Summoner in a crawlable Match found, so return null
        return null;
    }

    /* Attempts to make the request from the API and returns the obtained Object which must be casted.
       If a RiotApiException occurs which is a RateLimitException then we throttle the request (sleep until can make more requests)
       and repeat the request when we can make more requests to the API.
       Any other errors are returned, along with further errors after throttling and repeating the request.
    */
    private Object runApiMethodWithThrottle(ApiMethod method) throws RiotApiException {
        try {
            // Attempt to run the ApiMethod from the RiotApi instance
            return api.callCustomApiMethod(method);
        }
        catch (RiotApiException e) { // Error occured
            if (e instanceof RateLimitException) { // Reached rate limit, so attempt to throttle
                try {
                    // Obtain milliseconds to throttle for which is the retry after seconds multiplied by 1000 to get millis then 1.1 as a scalar bias to ensure we have waited long enough
                    long throttleMillis = (long) (((RateLimitException) e).getRetryAfter() * 1000 * 1.1);
                    log.warning("Rate limit reached! Throttling for " + throttleMillis + "ms!");
                    // Sleep the Thread to throttle
                    Thread.sleep(throttleMillis);
                    // Attempt to run the ApiMethod again from the RiotApi instance (retry).
                    return api.callCustomApiMethod(method);
                }
                catch (InterruptedException $) { // Interrupted when throttling, so log this issue and rethrow the original Exception
                    log.severe("[FATAL ERROR] Thread interrupted while sleeping to throttle! Rethrowing original RateLimitException, not handling!");
                    throw e;
                }
            }
            // Not a RateLimitException so rethrow the Exception since we cannot handle it here.
            throw e;
        }
    }

    // Set crawling to false to stop crawling (may call asynchronously during execution of crawl).
    public void stop() {
        this.crawling = false;
        log.severe("[STOPPING CRAWLER] stop() CALLED");
    }

    // Setter for the crawler config, if one wishes to change it (may change asynchronously during execution of crawl).
    public void setCrawlerConfig(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
        log.severe("[CONFIGURATION CHANGED] UPDATED CRAWLER CONFIG");
    }

    // Setter for the output handler, if one wishes to change it (may change asynchronously during execution of crawl).
    public void setOutputHandler(OutputHandler outputHandler) {
        this.outputHandler = outputHandler;
        log.severe("[CONFIGURATION CHANGED] UPDATED OUTPUT HANDLER");
    }
}
