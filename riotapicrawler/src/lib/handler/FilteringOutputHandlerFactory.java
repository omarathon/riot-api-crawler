package com.omarathon.riotapicrawler.src.lib.handler;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.src.lib.filter.MatchFilter;

public class FilteringOutputHandlerFactory {
    public static FilteringOutputHandler get(MatchFilter matchFilter, OutputHandler outputHandler) {
        return new FilteringOutputHandler(matchFilter) {
            @Override
            public void handle(Match input) {
                outputHandler.handle(input);
            }
        };
    }
}
