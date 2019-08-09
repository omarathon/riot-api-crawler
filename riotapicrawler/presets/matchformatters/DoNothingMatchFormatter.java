/*
    A MatchFormatter that simply passes through the input match without any modification.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchformatters;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.src.lib.formatter.MatchFormatter;

public class DoNothingMatchFormatter extends MatchFormatter<Match> {
    public Match format(Match m) {
        // pass through match without modification
        return m;

    }
}
