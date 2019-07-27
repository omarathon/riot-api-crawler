/*
   A configuration class for the crawler.

   Stores a MatchFilter and SummonerFilter used by the crawler to
   predicate the crawled Matches and Summoners,
   and an integer maxMatches property which determines the number of recent matches to obtain and
   send to the output handler for each crawled player.
   ("max" because a low level player may not have as many matches as the property, so it will just
   obtain all of them).
*/

package com.omarathon.riotapicrawler.src.lib;

public class CrawlerConfig {
    // Number of recent matches to obtain and send to the output handler for each crawled player
    private int maxMatches;
    // Predicate for the Match objects to crawl
    private MatchFilter matchFilter;
    // Predicate for the Summoner objects to crawl
    private SummonerFilter summonerFilter;

    /* INPUTS: MatchFilter, SummonerFilter and maxMatches properties.
       THROWS: IllegalArgumentException if:
        - the input maxMatches is >100 in which case the api cannot obtain so many,
        - maxMatches is <1 in which case nonsensical input. */
    public CrawlerConfig(MatchFilter mf, SummonerFilter sf, int maxMatches) throws IllegalArgumentException {
        // Set the maxMatches using setter method which may throw IllegalArgumentException
        setMaxMatches(maxMatches);
        // Set the class properties
        this.matchFilter = mf;
        this.summonerFilter = sf;
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
}
