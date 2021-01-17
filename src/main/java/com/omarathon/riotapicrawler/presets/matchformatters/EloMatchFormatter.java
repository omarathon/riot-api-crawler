/*
    Uses the CommonMaxEloEstimator to retrieve an elo estimate for the match and appends it to the entry
 */

package com.omarathon.riotapicrawler.presets.matchformatters;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.presets.matchformatters.lib.MatchEloData;
import com.omarathon.riotapicrawler.presets.util.Rank;
import com.omarathon.riotapicrawler.presets.util.estimators.lib.MatchEloEstimator;
import com.omarathon.riotapicrawler.src.lib.formatter.MatchFormatter;

public class EloMatchFormatter extends MatchFormatter<MatchEloData> {
    // Used to obtain Rank estimations from input Matches.
    private MatchEloEstimator estimator;

    public EloMatchFormatter(MatchEloEstimator estimator) {
        this.estimator = estimator;
    }

    public MatchEloData format(Match m) {
        // Rank estimate
        Rank rank = estimator.getEstimate(m);

        return new MatchEloData(m, rank);
    }
}
