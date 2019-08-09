package com.omarathon.riotapicrawler.src;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Platform;
import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.omarathon.riotapicrawler.presets.listeners.DefaultCrawlerListener;
import com.omarathon.riotapicrawler.src.lib.CrawlerConfig;
import com.omarathon.riotapicrawler.src.lib.CrawlerListener;
import com.omarathon.riotapicrawler.src.lib.SummonerHistory;
import com.omarathon.riotapicrawler.src.lib.filter.SummonerFilter;
import com.omarathon.riotapicrawler.src.lib.handler.OutputHandler;

import java.util.ArrayList;
import java.util.Random;

public class Crawler {
    private CrawlerConfig crawlerConfig;
    private OutputHandler outputHandler;
    private CrawlerListener listener;

    public enum Mode {
        IDLE, BACKTRACKING, CRAWLING
    }
    private Mode mode;

    public Crawler(CrawlerConfig crawlerConfig, OutputHandler outputHandler) {
        this(crawlerConfig, outputHandler, new DefaultCrawlerListener());
    }

    public Crawler(CrawlerConfig crawlerConfig, OutputHandler outputHandler, CrawlerListener listener) {
        // Set the output handler and crawler config to the input ones
        this.crawlerConfig = crawlerConfig;
        this.outputHandler = outputHandler;
        this.listener = listener;
        this.mode = Mode.IDLE;
    }

    public void run(String summonerName, Platform platform) {
        mode = Mode.CRAWLING;
        // Obtain input base summoner
        Summoner summoner = Orianna.summonerNamed(summonerName).withPlatform(platform).get();
        SummonerFilter summonerFilter = crawlerConfig.getSummonerFilter();
        if (summonerFilter.apply(summoner)) { // Crawlable
            listener.onInitialSummonerCrawlable(summoner);
            // Open a new thread and begin a crawl from the input player on such thread, with the mode set to CRAWLING
            mode = Mode.CRAWLING;
            Thread crawlThread = new Thread(() -> {
                listener.onInitialCrawlEntry(summoner);
                crawl(summoner);
            });
            crawlThread.start();
        }
        else { // Not crawlable
            listener.onInitialSummonerNotCrawlable(summoner);
            mode = Mode.IDLE;
        }
    }

    private void crawl(Summoner summoner) {
        while (mode != Mode.IDLE) {
            listener.onCrawlSummoner(summoner);
            MatchHistory matchHistory = null;
            switch (mode) {
                case BACKTRACKING:
                    listener.onBacktracking(summoner);
                    matchHistory = backtrack(summoner);
                    if (matchHistory == null) { // no next summoner so stop crawling
                        listener.onBacktrackFail(summoner);
                        stop();
                        continue;
                    }
                    listener.onBacktrackSuccess(summoner, matchHistory);
                    mode = Mode.CRAWLING;
                    break;

                case CRAWLING:
                    listener.onCrawling(summoner);
                    matchHistory = summoner.matchHistory()
                            .withEndIndex(crawlerConfig.getMaxMatches())
                            .get();
                    listener.onObtainedMatchHistory(summoner, matchHistory);
                    crawlerConfig.getSummonerHistory().addVisitedSummoner(summoner, matchHistory);
                    listener.onHandleMatchHistory(matchHistory);
                    outputHandler.handleMultiple(matchHistory);
                    break;
            }

            listener.onSeekNextCrawl(matchHistory);
            Summoner nextSummoner = seekNextCrawl(matchHistory);
            if (nextSummoner == null) {
                listener.onSeekNextCrawlFail(matchHistory);
                mode = Mode.BACKTRACKING;
            } else {
                listener.onSeekNextCrawlSuccess(matchHistory, nextSummoner);
                summoner = nextSummoner;
            }
        }
        listener.onEndCrawl();
    }

    private MatchHistory backtrack(Summoner summoner) {
        SummonerHistory history = crawlerConfig.getSummonerHistory();

        MatchHistory summonerMatchHistory = history.getMatchHistory(summoner);
        history.removeVisitedSummoner(summoner);
        MatchHistory backtrackMatchHistory= history.getRandomMatchHistory();
        history.addVisitedSummoner(summoner, summonerMatchHistory);

        return backtrackMatchHistory;
    }

    private Summoner seekNextCrawl(MatchHistory matchHistory) {
        // can instantly reject of MatchHistory GhostObject doesn't exist
        if (!matchHistory.exists()) {
            listener.onMatchHistoryNotExist(matchHistory);
            return null;
        }

        for (Match match : matchHistory) {
            listener.onProcessMatch(match);
            if (crawlerConfig.getMatchFilter().apply(match)) {
                listener.onCrawlableMatch(match);
                ArrayList<Participant> participants = new ArrayList<>();
                for (Participant participant : match.getParticipants()) {
                    participants.add(participant);
                }
                while (!participants.isEmpty()) {
                    Participant participant = participants.get(new Random().nextInt(participants.size()));
                    listener.onProcessParticipant(participant, match);
                    Summoner summoner = participant.getSummoner();
                    if (crawlerConfig.getSummonerFilter().apply(summoner)
                        && !crawlerConfig.getSummonerHistory().wasVisited(summoner)) {
                        listener.onCrawlableParticipantFound(participant, summoner, match);
                        return summoner;
                    }
                    else {
                        listener.onNotCrawlableParticipantFound(participant, summoner, match);
                        participants.remove(participant);
                    }
                }
            }
            else {
                listener.onNotCrawlableMatch(match);
            }
        }
        listener.onNoNextSummoner(matchHistory);
        // reach if no next summoner to crawl
        return null;
    }

    public void stop() {
        listener.onStop();
        mode = Mode.IDLE;
    }

    public Mode getMode() {
        return mode;
    }

    public void setCrawlerConfig(CrawlerConfig crawlerConfig) {
        listener.onCrawlerConfigUpdate(this.crawlerConfig, crawlerConfig);
        this.crawlerConfig = crawlerConfig;
    }

    public void setOutputHandler(OutputHandler outputHandler) {
        listener.onOutputHandlerUpdate(this.outputHandler, outputHandler);
        this.outputHandler = outputHandler;
    }

    public void setListener(CrawlerListener listener) {
        listener.onListenerUpdate(this.listener, listener);
        this.listener = listener;
    }
}
