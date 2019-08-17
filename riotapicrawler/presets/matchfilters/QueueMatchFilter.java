package com.omarathon.riotapicrawler.presets.matchfilters;

import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.src.lib.filter.MatchFilter;

import java.util.Set;

public class QueueMatchFilter extends MatchFilter {
    private Set<Queue> matchQueues;

    public QueueMatchFilter(Set<Queue> matchQueues) {
        super();
        this.matchQueues = matchQueues;
    }

    public boolean filter(Match m) {
        return (matchQueues.contains(m.getQueue()));
    }
}