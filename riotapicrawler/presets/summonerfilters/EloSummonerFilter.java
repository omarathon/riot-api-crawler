/*
    A SummonerFilter that only accepts summoners of a certain rank (rank, tier pair) on a given queueType
*/


package com.omarathon.riotapicrawler.presets.summonerfilters;

import com.omarathon.riotapicrawler.src.lib.SummonerFilter;
import com.omarathon.riotapicrawler.src.lib.helper.EloGetter;
import javafx.util.Pair;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

public class EloSummonerFilter implements SummonerFilter {
    // the rank to filter by
    private String rank;
    // the tier in the rank to filter by
    private String tier;
    // whether tier is being considered
    private boolean considerTier;
    // the queue type for the rank
    String queueType;

    // Constructor for both a rank and a tier
    public EloSummonerFilter(String rank, String tier, String queueType) {
        this.rank = rank;
        this.tier = tier;
        this.considerTier = true;
        this.queueType = queueType;
    }

    // Constructor for just a rank
    public EloSummonerFilter(String rank, String queueType) {
        this.rank = rank;
        this.considerTier = false;
        this.queueType = queueType;
    }

    public boolean filter(Summoner s, Platform p, RiotApi api) {
        // attempt to obtain <Rank, Tier> pair via the EloGetter helper class
        Pair<String, String> rankTierPair = null;
        try {
            rankTierPair = EloGetter.getElo(api, s, p, queueType);
        }
        catch (RiotApiException e) { // Error obtaining their elo on the queue, so return false to reject (cannot determine elo)
            return false;
        }
        // check whether the rankTierPair is null, in which case they have no rank on the given queue. so reject since cannot determine elo
        if (rankTierPair == null) return false;

        // successfully obtained rank and tier of summoner on given queue, so check if it matches.
        if (rankTierPair.getKey() == rank) {
            if (considerTier) return (rankTierPair.getValue() == tier);
            return true;
        }
        return false;
    }
}
