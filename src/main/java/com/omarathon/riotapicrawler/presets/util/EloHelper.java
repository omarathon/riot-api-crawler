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
    // Requires the RiotApi instance to make the api calls
    // Obtains a list of Ranks corresponding to different ranks on different league queues
    // for a Summoner.
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
        // for each leagueposition, add to ranks the corresponding Rank
        for (LeagueEntry leagueEntry : validEntries) {
            ranks.add(new Rank(leagueEntry.getTier(), leagueEntry.getDivision()));
        }

        return ranks;
    }

    // Given a set of input ranks, will return the maximum rank out of the input Ranks
    // Throws IllegalArgumentException if input set of ranks is empty
    public static Rank getMaxRank(Set<Rank> ranks) throws IllegalArgumentException {
        if (ranks.isEmpty()) throw new IllegalArgumentException("No ranks to obtain ranks from!");

        // Firstly obtains the maximum tier out of the ranks
        Tier maxTier = getMaxTier(ranks);

        // Now finds the maximum division within this max tier by getMaxDivision, using the above maxTier as the focus tier
        Division maxDivision = getMaxDivision(ranks, maxTier);

        return new Rank(maxTier, maxDivision);
    }

    // Given a set of input Ranks, will return the maximum tier (e.g DIAMOND > PLATINUM) out of the input Ranks
    // Throws IllegalArgumentException if input set of ranks is empty
    public static Tier getMaxTier(Set<Rank> ranks) throws IllegalArgumentException {
        if (ranks.isEmpty()) throw new IllegalArgumentException("No ranks to obtain tiers from!");
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
    // Throws IllegalArgumentException if input set of ranks is empty.
    // Returns null if there are no ranks in the given focus tier.
    public static Division getMaxDivision(Set<Rank> ranks, Tier focusTier) throws IllegalArgumentException {
        if (ranks.isEmpty()) throw new IllegalArgumentException("No ranks to obtain divisions from!");

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
