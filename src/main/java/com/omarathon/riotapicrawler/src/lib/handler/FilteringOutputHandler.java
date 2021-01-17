package com.omarathon.riotapicrawler.src.lib.handler;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.omarathon.riotapicrawler.presets.matchfilters.AllowAllMatchFilter;
import com.omarathon.riotapicrawler.src.lib.filter.MatchFilter;

public abstract class FilteringOutputHandler extends OutputHandler {
    private MatchFilter filter;

    public FilteringOutputHandler() {
        this(new AllowAllMatchFilter());
    }

    public FilteringOutputHandler(MatchFilter filter) {
        setMatchFilter(filter);
    }

    @Override
    public void apply(Match match) {
        if (filter.apply(match)) super.apply(match);
    }

    public void setMatchFilter(MatchFilter matchFilter) {
        this.filter = matchFilter;
    }

    public MatchFilter getMatchFilter() {
        return filter;
    }

}

public class ExampleOutputHandler extends OutputHandler {
    public void handle(Match m) {
        // do something with the Match...
    }
}
