/*
    A SummonerFilter that simply allows all summoners.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.summonerfilters;

import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.omarathon.riotapicrawler.src.lib.filter.SummonerFilter;

public class AllowAllSummonerFilter extends SummonerFilter {
    public boolean filter(Summoner s) {
        // Allow all summoners, so the filter returns true for all input summoners.
        return true;
    }
}
