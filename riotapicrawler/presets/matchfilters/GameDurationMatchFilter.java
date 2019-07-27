/*
    A MatchFilter that rejects Matches shorter than 20 minutes.
*/

package com.omarathon.riotapicrawler.presets.matchfilters;

import com.omarathon.riotapicrawler.src.lib.MatchFilter;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.match.dto.Match;

public class GameDurationMatchFilter implements MatchFilter {
    public boolean filter(Match m, RiotApi api) {
        // If the duration of the match is at least 60*20 seconds then return true, i.e allow, otherwise return false, i.e reject
        return (m.getGameDuration() >= 60*20);
    }
}
