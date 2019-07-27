/*
    A "more advanced" CrawlerConfig that:

    - uses the GameDurationMatchFilter, such that only Matches with duration 20 minutes and beyond are crawled,
    - uses the LevelSummonerFilter, such that only Summoners with level 30 and beyond are crawled,

    and takes as input the number of recent matches per player to be obtained and stored by the crawler.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.crawlerconfigs;

import com.omarathon.riotapicrawler.presets.matchfilters.GameDurationMatchFilter;
import com.omarathon.riotapicrawler.presets.summonerfilters.LevelSummonerFilter;
import com.omarathon.riotapicrawler.src.lib.CrawlerConfig;

public class AdvancedCrawlerConfig extends CrawlerConfig {
    // Constructor takes the max matches only (cannot really assume this parameter)
    public AdvancedCrawlerConfig(int maxMatches) {
        /* Construct a CrawlerConfig with a GameDurationMatchFilter and a LevelSummonerFilter
           and pass through the input max matches parameter. */
        super(new GameDurationMatchFilter(), new LevelSummonerFilter(), maxMatches);
    }
}
