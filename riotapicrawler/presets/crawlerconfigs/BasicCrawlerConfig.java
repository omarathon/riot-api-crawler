/*
    A CrawlerConfig that allows all matches and all summoners to be crawled,
    and takes as input the number of recent matches per player to be obtained and stored by the crawler.
*/

package com.omarathon.riotapicrawler.presets.crawlerconfigs;

import com.omarathon.riotapicrawler.presets.matchfilters.AllowAllMatchFilter;
import com.omarathon.riotapicrawler.presets.summonerfilters.AllowAllSummonerFilter;
import com.omarathon.riotapicrawler.src.lib.CrawlerConfig;

public class BasicCrawlerConfig extends CrawlerConfig {
    // Constructor takes the max matches only (cannot really assume this parameter)
    public BasicCrawlerConfig(int maxMatches) {
        /* Construct a CrawlerConfig with an AllowAllMatchFilter and an AllowAllSummonerFilter
           such that all matches and summoners are crawlable, and pass through the input max matches parameter. */
        super(new AllowAllMatchFilter(), new AllowAllSummonerFilter(), maxMatches);
    }
}
