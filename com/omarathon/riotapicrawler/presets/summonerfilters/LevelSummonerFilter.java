/*
    A SummonerFilter that rejects all summoners with level less than 30.
*/

package com.omarathon.riotapicrawler.presets.summonerfilters;

import com.omarathon.riotapicrawler.src.lib.SummonerFilter;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

public class LevelSummonerFilter implements SummonerFilter  {
    public boolean filter(Summoner s, Platform p) {
        // If their summoner level if at least 30 than allow them, i.e return true, otherwise reject, i.e return false.
        return (s.getSummonerLevel() >= 30);
    }
}
