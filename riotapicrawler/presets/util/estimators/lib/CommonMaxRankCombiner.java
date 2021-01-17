/*
    A RankCombiner which will find the most common tier (e.g PLATINUM),
    then will obtain the maximum division among the ranks there (e.g max is PLATINUM 2).

    Named "CommonMax" because we first seek the most common tier, then return the maximum division within such tier.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
 */

package com.omarathon.riotapicrawler.presets.util.estimators.lib;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Tier;
import com.omarathon.riotapicrawler.presets.util.EloHelper;
import com.omarathon.riotapicrawler.presets.util.Rank;

import java.util.HashMap;
import java.util.Set;

public class CommonMaxRankCombiner implements RankCombiner {
    public Rank combine(Set<Rank> ranks) throws IllegalArgumentException {
        if (ranks.isEmpty()) throw new IllegalArgumentException("No ranks entered, cannot combine!");

        // store count for each tier in hashmap
        HashMap<Tier, Integer> tierCounts = new HashMap<>();
        // maximum tier with its count
        Tier maxTier = null;
        int maxTierCount = -1;

        // for each rank, obtain its tier and update its entry in the hashmap
        for (Rank rank : ranks) {
            Tier tier = rank.getTier();

            // if already in hashmap, obtain value, increment and replace with incremented value
            if (tierCounts.containsKey(tier)) {
                Integer count = tierCounts.get(tier);
                tierCounts.put(tier, count++);
            }
            else { // is a new tier in the hashmap, so set its count to 0
                tierCounts.put(tier, 0);
            }

            // obtain updated count. check if is new max, if so replace. if same as max, pick the greatest rank.
            Integer updatedCount = tierCounts.get(tier);
            if (updatedCount > maxTierCount) { // guaranteed on first iteration since it will be at least 0, which is >-1
                maxTier = tier;
            }
            else if (updatedCount == maxTierCount) {
                if (tier.compareTo(maxTier) > 0) maxTier = tier;
            }
        }

        // we now have the the most common tier. we now seek the maximum division of such tier within the given list of ranks.
        Division maxDivision = EloHelper.getMaxDivision(ranks, maxTier);

        return new Rank(maxTier, maxDivision);
    }
}
