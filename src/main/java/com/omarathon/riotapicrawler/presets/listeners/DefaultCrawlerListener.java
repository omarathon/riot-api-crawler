package com.omarathon.riotapicrawler.presets.listeners;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.omarathon.riotapicrawler.src.lib.CrawlerConfig;
import com.omarathon.riotapicrawler.src.lib.CrawlerListener;
import com.omarathon.riotapicrawler.src.lib.handler.OutputHandler;

public class DefaultCrawlerListener implements CrawlerListener {
    protected String runPrefix = "[RUN] ";
    @Override
    public void onInitialSummonerCrawlable(Summoner initialSummoner) {
        System.out.println(runPrefix + "Input Summoner: " + initialSummoner.toString() + " is crawlable!");
    }

    @Override
    public void onInitialSummonerNotCrawlable(Summoner initialSummoner) {
        System.out.println(runPrefix + "Input Summoner: " + initialSummoner.toString() + " is NOT crawlable, aborting!");
    }

    @Override
    public void onInitialCrawlEntry(Summoner initialSummoner) {
        System.out.println(runPrefix + "Initiating crawl on input Summoner: " + initialSummoner.toString());
    }


    protected String crawlPrefix = "[CRAWL] ";
    @Override
    public void onCrawlSummoner(Summoner crawlingSummoner) {
        System.out.println(crawlPrefix + "Began crawling Summoner: " + crawlingSummoner.toString());
    }

    @Override
    public void onBacktracking(Summoner fromSummoner) {
        System.out.println(crawlPrefix + "Backtracking from Summoner: " + fromSummoner.toString());
    }

    @Override
    public void onBacktrackFail(Summoner fromSummoner) {
        System.out.println(crawlPrefix + "Failed backtracking from Summoner: " + fromSummoner.toString());
    }

    @Override
    public void onBacktrackSuccess(Summoner fromSummoner, MatchHistory seekNextMatchHistory) {
        System.out.println(crawlPrefix + "Successfully backtracked from Summoner: " + fromSummoner + " and obtained seeking next crawl in MatchHistory: " + seekNextMatchHistory.toString());
    }

    @Override
    public void onCrawling(Summoner crawlingSummoner) {
        System.out.println(crawlPrefix + "Are crawling from Summoner: " + crawlingSummoner.toString());
    }

    @Override
    public void onObtainedMatchHistory(Summoner crawlingSummoner, MatchHistory crawlingSummonerMatchHistory) {
        System.out.println(crawlPrefix + "Obtained MatchHistory: " + crawlingSummonerMatchHistory + " from crawling Summoner: " + crawlingSummoner.toString());
    }

    @Override
    public void onHandleMatchHistory(MatchHistory crawlingSummonerMatchHistory) {
        System.out.println(crawlPrefix + "Sent MatchHistory: " + crawlingSummonerMatchHistory + " to OutputHandler!");
    }

    @Override
    public void onSeekNextCrawl(MatchHistory nextCrawlMatchHistory) {
        System.out.println(crawlPrefix + "Seeking the next Summoner to crawl from MatchHistory: " + nextCrawlMatchHistory.toString());
    }

    @Override
    public void onSeekNextCrawlFail(MatchHistory nextCrawlMatchHistory) {
        System.out.println(crawlPrefix + "Failed obtaining next Summoner to crawl from MatchHistory: " + nextCrawlMatchHistory.toString());
    }

    @Override
    public void onSeekNextCrawlSuccess(MatchHistory nextCrawlMatchHistory, Summoner nextSummoner) {
        System.out.println(crawlPrefix + "Successfully obtained next Summoner to crawl: " + nextSummoner.toString() + " from MatchHistory: " + nextCrawlMatchHistory.toString() + " - shall crawl them now!");
    }

    @Override
    public void onEndCrawl() {
        System.out.println(crawlPrefix + "Exited main crawling loop - crawler mode set to IDLE - stopping crawling!!");
    }


    protected String seekNextCrawlPrefix = "[SEEKNEXTCRAWL] ";
    @Override
    public void onMatchHistoryNotExist(MatchHistory notExistingMatchHistory) {
        System.out.println(seekNextCrawlPrefix + "MatchHistory: " + notExistingMatchHistory.toString() + " doesn't exist! Aborting seek!");
    }

    @Override
    public void onProcessMatch(Match matchBeingProcessed) {
        System.out.println(seekNextCrawlPrefix + "Processing Match: " + matchBeingProcessed.toString() + " from MatchHistory.");
    }

    @Override
    public void onCrawlableMatch(Match crawlableMatch) {
        System.out.println(seekNextCrawlPrefix + "Match: " + crawlableMatch + " is crawlable - proceeding to search for a crawlable participant!");
    }

    @Override
    public void onNotCrawlableMatch(Match notCrawlableMatch) {
        System.out.println(seekNextCrawlPrefix + "Match: " + notCrawlableMatch + " isn't crawlable - moving to next Match!");
    }

    @Override
    public void onProcessParticipant(Participant participantBeingProcessed, Match matchBeingProcessed) {
        System.out.println(seekNextCrawlPrefix + "Processing Participant: " + participantBeingProcessed.toString() + " from Match: " + matchBeingProcessed.toString());
    }

    @Override
    public void onCrawlableParticipantFound(Participant crawlableParticipant, Summoner summonerOfParticipant, Match matchOfParticipant) {
        System.out.println(seekNextCrawlPrefix + "Found crawlable Participant: " + crawlableParticipant.toString() + " with Summoner: " + summonerOfParticipant.toString() + " from Match: " + matchOfParticipant.toString() + " - returning Summoner as next crawl!");
    }

    @Override
    public void onNotCrawlableParticipantFound(Participant notCrawlableParticipant, Summoner summonerOfParticipant, Match matchOfParticipant) {
        System.out.println(seekNextCrawlPrefix + "Participant: " + notCrawlableParticipant.toString() + " with Summoner: " + summonerOfParticipant.toString() + " from Match: " + matchOfParticipant.toString() + " isn't crawlable - moving to next participant!");
    }

    @Override
    public void onNoNextSummoner(MatchHistory existingButFailingMatchHistory) {
        System.out.println(seekNextCrawlPrefix + "No next Summoner to crawl found from MatchHistory: " + existingButFailingMatchHistory.toString() + " - aborting seek!");
    }


    protected String stopPrefix = "[STOP] ";
    @Override
    public void onStop() {
        System.out.println(stopPrefix + "stop() called - stopping crawler!!");
    }


    protected String setCrawlerConfigPrefix = "[SETCRAWLERCONFIG] ";
    @Override
    public void onCrawlerConfigUpdate(CrawlerConfig oldCrawlerConfig, CrawlerConfig newCrawlerConfig) {
        System.out.println(setCrawlerConfigPrefix + "Updated CrawlerConfig!!");
    }


    protected String setOutputHandlerPrefix = "[SETOUTPUTHANDLER] ";
    @Override
    public void onOutputHandlerUpdate(OutputHandler oldOutputHandler, OutputHandler newOutputHandler) {
        System.out.println(setOutputHandlerPrefix + "Updated OutputHandler!!");
    }


    protected String setListenerPrefix = "[SETLISTENER] ";
    @Override
    public void onListenerUpdate(CrawlerListener oldListener, CrawlerListener newListener) {
        System.out.println(setListenerPrefix + "Updated Listener!!");
    }
}
