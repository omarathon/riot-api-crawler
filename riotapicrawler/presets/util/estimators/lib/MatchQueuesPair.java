package com.omarathon.riotapicrawler.presets.util.estimators.lib;

import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.core.match.Match;

import java.util.Set;

// Stores a Match and a set of League Queues as a pair
public class MatchQueuesPair {
    private Match match;
    private Set<Queue> leagueQueues;

    public MatchQueuesPair(Match match, Set<Queue> leagueQueues) {
        this.match = match;
        this.leagueQueues = leagueQueues;
    }

    public Match getMatch() {
        return match;
    }

    public Set<Queue> getLeagueQueues() {
        return leagueQueues;
    }
}
