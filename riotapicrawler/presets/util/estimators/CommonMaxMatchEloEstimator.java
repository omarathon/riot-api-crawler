/*
    Uses the CommonMaxRankCombiner to obtain a CommonMax estimate for the elo of a game.
 */

package com.omarathon.riotapicrawler.presets.util.estimators;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.omarathon.riotapicrawler.presets.util.Rank;
import com.omarathon.riotapicrawler.presets.util.estimators.lib.CommonMaxRankCombiner;
import com.omarathon.riotapicrawler.presets.util.estimators.lib.MatchEloEstimator;
import com.omarathon.riotapicrawler.presets.util.estimators.lib.SummonerEloEstimator;

import java.util.HashSet;
import java.util.Set;

// estimates ranks based upon input matches, working across the league queues put in the constructor
public class CommonMaxMatchEloEstimator extends MatchEloEstimator {
    private CommonMaxRankCombiner rankCombiner;
    private SummonerEloEstimator summonerEloEstimator;

    // input a SummonerEloEstimator to compute the elo for each summoner, then shall take the Common-Max combination of such ranks obtained from that estimator.
    public CommonMaxMatchEloEstimator(SummonerEloEstimator summonerEloEstimator) {
        super();
        this.summonerEloEstimator = summonerEloEstimator;
        this.rankCombiner = new CommonMaxRankCombiner();
    }

    // estimates a Match's "Rank" by considering its players ranks across the given League Queues. Returns null if failed to estimate.
    public Rank estimate(Match match) {
        // obtain set of ranks of the players, where for each player we obtain the maximum rank out of the ones in the given queue types
        Set<Rank> ranks = new HashSet<>();

        // iterate over each Participant in the match, to obtain the Summoner for each player.
        for (Participant participant : match.getParticipants()) {
            Summoner summoner = participant.getSummoner();
            if (!summoner.exists()) continue;
            Rank summonerRank = summonerEloEstimator.getEstimate(summoner);
            if (summonerRank != null) ranks.add(summonerRank);
        }

        try {
            // now use the CommonMaxRankCombiner to combine the ranks of the players into a common-max Rank
            return rankCombiner.combine(ranks);
        }
        catch (IllegalArgumentException e) { // ranks was empty, so cannot estimate without any context - return null for no estimate
            return null;
        }
    }
}
