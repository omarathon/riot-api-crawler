/*
    A SummonerFilter that simply allows all summoners.
*/

package com.omarathon.riotapicrawler.presets.summonerfilters;

import com.omarathon.riotapicrawler.src.lib.SummonerFilter;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

public class AllowAllSummonerFilter implements SummonerFilter  {
    public boolean filter(Summoner s, Platform p, RiotApi api) {
        // Allow all summoners, so the filter returns true for all input summoners.
        return true;
    }
}
