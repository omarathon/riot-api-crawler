package com.omarathon.riotapicrawler.src.lib;

import com.omarathon.riotapicrawler.src.lib.filter.MatchFilter;
import com.omarathon.riotapicrawler.src.lib.filter.SummonerFilter;

public class CrawlerConfig {
    // Number of recent matches to obtain and send to the output handler for each crawled player
    private int maxMatches;
    // Predicate for the Match objects to crawl
    private MatchFilter matchFilter;
    // Predicate for the Summoner objects to crawl
    private SummonerFilter summonerFilter;

    private SummonerHistory summonerHistory;

    /* INPUTS: MatchFilter, SummonerFilter and maxMatches properties.
       THROWS: IllegalArgumentException if:
        - the input maxMatches is >100 in which case the api cannot obtain so many,
        - maxMatches is <1 in which case nonsensical input. */
    public CrawlerConfig(MatchFilter matchFilter, SummonerFilter summonerFilter, int maxMatches, SummonerHistory summonerHistory) throws IllegalArgumentException {
        // Set the maxMatches using setter method which may throw IllegalArgumentException
        setMaxMatches(maxMatches);
        // Set the class properties
        setMatchFilter(matchFilter);
        setSummonerFilter(summonerFilter);
        setSummonerHistory(summonerHistory);
    }

    public CrawlerConfig(MatchFilter matchFilter, SummonerFilter summonerFilter, int maxMatches) {
        this(matchFilter, summonerFilter, maxMatches, new SummonerHistory());
    }

    /*
        Getters and setters
     */

    public int getMaxMatches() {
        return maxMatches;
    }

    public MatchFilter getMatchFilter() {
        return matchFilter;
    }

    public SummonerFilter getSummonerFilter() {
        return summonerFilter;
    }

    public SummonerHistory getSummonerHistory() {
        return summonerHistory;
    }

    /* Setter for maxMatches THROWS IllegalArgumentException if:
        - the input maxMatches is >100 in which case the api cannot obtain so many,
        - maxMatches is <1 in which case nonsensical input. */
    public void setMaxMatches(int maxMatches) throws IllegalArgumentException {
        // Throw errors if input maxMatches is illegal
        if (maxMatches > 100) throw new IllegalArgumentException("Maximum matches to obtain for each player must be at most 100!");
        if (maxMatches < 1) throw new IllegalArgumentException("Nonsensical input for maximum matches to obtain for each player (less than 1)");
        // Input maxMatches is valid, so set the property
        this.maxMatches = maxMatches;
    }

    public void setMatchFilter(MatchFilter matchFilter) {
        this.matchFilter = matchFilter;
    }

    public void setSummonerFilter(SummonerFilter summonerFilter) {
        this.summonerFilter = summonerFilter;
    }

    public void setSummonerHistory(SummonerHistory summonerHistory) {
        this.summonerHistory = summonerHistory;
    }
}
