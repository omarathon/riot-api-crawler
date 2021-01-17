package com.omarathon.riotapicrawler.src.lib;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.omarathon.riotapicrawler.src.lib.handler.OutputHandler;

public interface CrawlerListener {
    /*
        run method listeners
     */

    // when the input summoner to the run method is crawlable
    void onInitialSummonerCrawlable(Summoner initialSummoner);

    // when the input summoner to the run method isn't crawlable
    void onInitialSummonerNotCrawlable(Summoner initialSummoner);

    // when crawl is called on the initial summoner
    void onInitialCrawlEntry(Summoner initialSummoner);


    /*
        crawl method listeners
     */

    // when the mode =/= IDLE (main crawling loop) is entered for an iteration with a summoner being crawled
    void onCrawlSummoner(Summoner crawlingSummoner);

    // when backtracking from a summoner
    void onBacktracking(Summoner fromSummoner);

    // when the backtracking failed from a summoner (no backtrack summoner)
    void onBacktrackFail(Summoner fromSummoner);

    // when the backtracking from a summoner was successful, and the new match history to seek the next crawl
    void onBacktrackSuccess(Summoner fromSummoner, MatchHistory seekNextMatchHistory);

    // when crawling from a summoner
    void onCrawling(Summoner crawlingSummoner);

    // after obtaining the match history for the crawling summoner
    void onObtainedMatchHistory(Summoner crawlingSummoner, MatchHistory crawlingSummonerMatchHistory);

    // before sending match history to output handler
    void onHandleMatchHistory(MatchHistory crawlingSummonerMatchHistory);

    // when seeking the next summoner to crawl from a match history
    void onSeekNextCrawl(MatchHistory nextCrawlMatchHistory);

    // when there was no summoner obtained to crawl next from a match history used to crawl next
    void onSeekNextCrawlFail(MatchHistory nextCrawlMatchHistory);

    // when there was a summoner obtained to crawl next from a match history
    void onSeekNextCrawlSuccess(MatchHistory nextCrawlMatchHistory, Summoner nextSummoner);

    // when the mode =/= IDLE (main crawling loop) s exited, i.e when the Mode becomes NULL and the crawling loop is exited
    void onEndCrawl();


    /*
        seekNextCrawl method listeners
     */

    // when the given match history doesn't exist
    void onMatchHistoryNotExist(MatchHistory notExistingMatchHistory);

    // when a match from the match history is beginning to be processed
    void onProcessMatch(Match matchBeingProcessed);

    // when the match being processed is crawlable
    void onCrawlableMatch(Match crawlableMatch);

    // when the match being processed is uncrawlable
    void onNotCrawlableMatch(Match notCrawlableMatch);

    // when a participant from a crawlable match is being processed
    void onProcessParticipant(Participant participantBeingProcessed, Match matchBeingProcessed);

    // when a participant from a crawlable match is found to be crawlable, i.e the instance before we return from seekNextCrawl
    void onCrawlableParticipantFound(Participant crawlableParticipant, Summoner summonerOfParticipant, Match matchOfParticipant);

    // when a participant from a crawlable match is found to not be crawlable
    void onNotCrawlableParticipantFound(Participant notCrawlableParticipant, Summoner summonerOfParticipant, Match matchOfParticipant);

    // when seekNextCrawl could not find a next summmoner to crawl from the given existing match history
    void onNoNextSummoner(MatchHistory existingButFailingMatchHistory);

    /*
        stop method listeners
     */

    // when stop() is called
    void onStop();


    /*
        setCrawlerConfig method listeners
     */

    // when setCrawlerConfig is called
    void onCrawlerConfigUpdate(CrawlerConfig oldCrawlerConfig, CrawlerConfig newCrawlerConfig);


    /*
        setOutputHandler method listeners
     */

    // when setOutputHandler is called
    void onOutputHandlerUpdate(OutputHandler oldOutputHandler, OutputHandler newOutputHandler);


    /*
        setListener method listeners
     */

    // when setListener is called
    void onListenerUpdate(CrawlerListener oldListener, CrawlerListener newListener);

}
