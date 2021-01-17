package com.omarathon.riotapicrawler.presets.matchformatters.lib;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.presets.util.Rank;

import java.io.Serializable;

public class MatchEloData implements Serializable {
    private Match match;
    private Rank rank;

    public MatchEloData(Match match, Rank rank) {
        this.match = match;
        this.rank = rank;
    }

    public Match getMatch() {
        return match;
    }

    public Rank getRank() {
        return rank;
    }
}
