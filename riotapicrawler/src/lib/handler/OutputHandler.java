package com.omarathon.riotapicrawler.src.lib.handler;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;

public abstract class OutputHandler implements Handler<Match> {
    public void apply(Match match) {
        handle(match);
    }

    public void applyMultiple(MatchHistory matchHistory) {
        for (Match match : matchHistory) {
            apply(match);
        }
    }
}
