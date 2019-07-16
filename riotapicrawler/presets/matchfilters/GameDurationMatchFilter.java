/*
    A MatchFilter that rejects Matches shorter than 20 minutes.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchfilters;

import com.omarathon.riotapicrawler.src.lib.MatchFilter;
import net.rithms.riot.api.endpoints.match.dto.Match;

public class GameDurationMatchFilter implements MatchFilter {
    public boolean filter(Match m) {
        // If the duration of the match is at least 60^20 seconds then return true, i.e allow, otherwise return false, i.e reject
        return (m.getGameDuration() >= 60*20);
    }
}
