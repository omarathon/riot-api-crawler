package com.omarathon.riotapicrawler.presets.util.estimators;

import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.omarathon.riotapicrawler.presets.util.EloHelper;
import com.omarathon.riotapicrawler.presets.util.Rank;
import com.omarathon.riotapicrawler.presets.util.estimators.lib.SummonerEloEstimator;

import java.util.Set;

public class MaxSummonerEloEstimator extends SummonerEloEstimator {
    private Set<Queue> leagueQueues;

    public MaxSummonerEloEstimator(Set<Queue> leagueQueues) {
        super();
        this.leagueQueues = leagueQueues;
    }

    public Rank estimate(Summoner summoner) {
        // attempt to obtain Ranks of the summoner on the given queueTypes via the EloHelper helper class
        Set<Rank> ranks = EloHelper.getElos(summoner, leagueQueues);

        // now obtain the maximum out of their ranks on the given queues, via EloHelper
        try {
            return EloHelper.getMaxRank(ranks);
        }
        catch (IllegalArgumentException e) { // ranks is empty, in which case they have no ranks on the given queues. so return null for failure in obtaining estimate
            return null;
        }
    }
}
