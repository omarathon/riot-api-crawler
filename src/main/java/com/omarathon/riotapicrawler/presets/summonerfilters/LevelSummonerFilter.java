/*
    A SummonerFilter that rejects all summoners with level less than 30.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.summonerfilters;

import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.omarathon.riotapicrawler.src.lib.filter.SummonerFilter;

public class LevelSummonerFilter extends SummonerFilter  {
    public boolean filter(Summoner s) {
        // If their summoner level if at least 30 than allow them, i.e return true, otherwise reject, i.e return false.
        return (s.getLevel() >= 30);
    }
}
