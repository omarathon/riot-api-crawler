package com.omarathon.riotapicrawler.src.lib.handler;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;

public abstract class OutputHandler implements Handler<Match> {
    public void handleMultiple(MatchHistory matchHistory) {
        for (Match match : matchHistory) {
            handle(match);
        }
    }
}
