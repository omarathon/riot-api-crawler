/*
    A MatchFilter that simply allows all matches.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchfilters;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.src.lib.filter.MatchFilter;

public class AllowAllMatchFilter extends MatchFilter {
    public boolean filter(Match m) {
        // Allow all matches, so the filter returns true for all input matches.
        return true;
    }
}
