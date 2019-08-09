/*
    A SummonerFilter that computes a Summoner's maximum rank across a given set of league queues
    (e.g ranked 5x5 solo, ranked 3x3 twisted treeline, etc).
    Then, determines whether it belongs to a set of accepted ranks. If so, accepts the Summoner, otherwise rejects.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/


package com.omarathon.riotapicrawler.presets.summonerfilters;

import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.omarathon.riotapicrawler.presets.util.EstimatingGhostFilter;
import com.omarathon.riotapicrawler.presets.util.Rank;
import com.omarathon.riotapicrawler.presets.util.estimators.lib.SummonerEloEstimator;
import com.omarathon.riotapicrawler.src.lib.filter.SummonerFilter;

import java.util.Set;

public class EloSummonerFilter extends SummonerFilter {
    private EstimatingGhostFilter<Summoner, Rank> filter;

    private EloSummonerFilter() { }

    public EloSummonerFilter(Set<Rank> filterRanks, SummonerEloEstimator estimator){
        this.filter = new EstimatingGhostFilter<>(filterRanks, estimator);
    }

    public boolean filter(Summoner s) {
        return filter.apply(s);
    }
}
