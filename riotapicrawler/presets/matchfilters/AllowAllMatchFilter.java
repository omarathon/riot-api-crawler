/*
    A MatchFilter that simply allows all matches.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchfilters;

import com.omarathon.riotapicrawler.src.lib.MatchFilter;
import net.rithms.riot.api.endpoints.match.dto.Match;

public class AllowAllMatchFilter implements MatchFilter {
    public boolean filter(Match m) {
        // Allow all matches, so the filter returns true for all input matches.
        return true;
    }
}
