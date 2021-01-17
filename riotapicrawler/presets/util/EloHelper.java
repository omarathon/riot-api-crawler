/*
   A helper class for general elo calculations.

   Author: Omar Tanner (omarathon)
   Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.util;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Tier;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.league.LeaguePositions;
import com.merakianalytics.orianna.types.core.summoner.Summoner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EloHelper {
    // Obtains a list of Ranks corresponding to different ranks on different league queues
    // for a Summoner. the rank is null if it was failed to be obtained.
    public static Set<Rank> getElos(Summoner s, Set<Queue> queueTypes) {
        // obtain league positions
        LeaguePositions leaguePositions = s.getLeaguePositions();
        // iterate through each league position to find the league entry within the specified queueTypes
        List<LeagueEntry> validEntries = new ArrayList<>();
        for (LeagueEntry leagueEntry : leaguePositions) {
            Queue queue = null;
            try {
                queue = leagueEntry.getQueue();
            }
            catch (IllegalArgumentException e) {
                continue;
            }
            if (queueTypes.contains(queue)) {
               validEntries.add(leagueEntry);
            }
        }

        Set<Rank> ranks = new HashSet<>();
        // for each LeagueEntry, add to ranks the corresponding Rank (is null if it can't be obtained)
        for (LeagueEntry leagueEntry : validEntries) {
            Tier tier = null;
            Division division = null;
            try { // attempt to obtain tier and division for leagueEntry. may throw some errors as Orianna may not have been updated for IRON tiers.
                tier = leagueEntry.getTier();
                division = leagueEntry.getDivision();
            }
            catch (Exception e) {
                ranks.add(null);
                continue;
            }
            ranks.add(new Rank(tier, division));
        }

        return ranks;
    }

    // returns true if the input Ranks are valid, otherwise false. they're invalid if there's no ranks in the set, or if they'll all null.
    private static boolean checkRanks(Set<Rank> ranks) {
        if (ranks.isEmpty()) return false;
        boolean foundNonNullRank = false;
        for (Rank rank : ranks) {
            if (rank != null) {
                foundNonNullRank = true;
                break;
            }
        }
        if (!foundNonNullRank) return false;

        return true;
    }
    // Given a set of input ranks, will return the maximum rank out of the input Ranks
    // Throws IllegalArgumentException if input set of ranks is empty or they're all null
    public static Rank getMaxRank(Set<Rank> ranks) throws IllegalArgumentException {
        if (!checkRanks(ranks)) throw new IllegalArgumentException("Input ranks is either empty or entirely null");

        // Firstly obtains the maximum tier out of the ranks
        Tier maxTier = getMaxTier(ranks);

        // Now finds the maximum division within this max tier by getMaxDivision, using the above maxTier as the focus tier
        Division maxDivision = getMaxDivision(ranks, maxTier);

        return new Rank(maxTier, maxDivision);
    }

    // Given a set of input Ranks, will return the maximum tier (e.g DIAMOND > PLATINUM) out of the input Ranks
    // Throws IllegalArgumentException if input set of ranks is empty or they're all null
    public static Tier getMaxTier(Set<Rank> ranks) throws IllegalArgumentException {
        if (!checkRanks(ranks)) throw new IllegalArgumentException("Input ranks is either empty or entirely null");

        Tier maxTier = null;
        for (Rank summonerRank : ranks) {
            Tier summonerTier = summonerRank.getTier();
            if (maxTier == null) maxTier = summonerTier;
            // this tier > max tier means replace max tier with this tier
            else if (summonerTier.compareTo(maxTier) > 0) maxTier = summonerTier;
        }
        return maxTier;
    }

    // Given a set of input Ranks, and a chosen focus tier,
    // will return the maximum division found for that tier within the set.
    // Throws IllegalArgumentException if input set of ranks is empty or they're all null
    // Returns null if there are no ranks in the given focus tier.
    public static Division getMaxDivision(Set<Rank> ranks, Tier focusTier) throws IllegalArgumentException {
        if (!checkRanks(ranks)) throw new IllegalArgumentException("Input ranks is either empty or entirely null");

        Division maxDivision = null;

        for (Rank rank : ranks) {
            // check if this rank is of the max (most common) tier
            if (rank.getTier().equals(focusTier)) {
                Division division = rank.getDivision();
                if (maxDivision == null) maxDivision = division;
                else if (division.compareTo(maxDivision) > 0) maxDivision = division;
            }
        }
        // return division at the given division id
        return maxDivision;
    }
}
