/*
    A MatchFilter that computes an estimated elo (Rank) of the Match,
    and only accepts it if it's within one of a given set of accepted Ranks.

    Computes estimates elo via the CommonMax RankCombiner, found within lib.
    the estimated rank has tier the most common tier among the ranks in the game,
    and division the maximum among the ranks in the game with the most common tier.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchfilters;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.presets.util.EstimatingGhostFilter;
import com.omarathon.riotapicrawler.presets.util.Rank;
import com.omarathon.riotapicrawler.presets.util.estimators.lib.MatchEloEstimator;
import com.omarathon.riotapicrawler.src.lib.filter.MatchFilter;

import java.util.Set;

public class EloMatchFilter extends MatchFilter {
    private EstimatingGhostFilter<Match, Rank> filter;

    private EloMatchFilter() { }

    public EloMatchFilter(Set<Rank> filterRanks, MatchEloEstimator estimator) {
        this.filter = new EstimatingGhostFilter<>(filterRanks, estimator);
    }

    public boolean filter(Match m) {
        return filter.apply(m);
    }
}
