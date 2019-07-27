/*
    A MatchFilter that only allows matches with a given average elo.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchfilters;

import com.omarathon.riotapicrawler.src.lib.MatchFilter;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.Participant;

import java.util.Arrays;
import java.util.List;

public class AverageEloMatchFilter implements MatchFilter {
    // The possible ranks
    private List<String> ranks = Arrays.asList("UNRANKED", "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND", "MASTER", "GRANDMASTER", "CHALLENGER");
    // The index in the above list of the rank wishing to be filtered by
    private int rankIndex;

    // INPUT: rank as string to filter by.
    // THROWS: IllegalArgumentException if input rank isn't a rank. (convert to uppercase to allow any casing)
    public AverageEloMatchFilter(String rank) throws IllegalArgumentException {
        rank = rank.toUpperCase();
        int rankIndex = ranks.indexOf(rank);
        // not in the list of ranks
        if (rankIndex == -1) throw new IllegalArgumentException("Invalid input rank!");
        // is in the list of ranks, so set the rankIndex
        this.rankIndex = rankIndex;
    }

    public boolean filter(Match m, RiotApi api) {
        // number of non-null ranks
        int rankCount = 0;
        // total of non-null rank indexes
        int totalRank = 0;
        // iterate over each participant
        for (Participant participant : m.getParticipants()) {
            // obtain rank
            String participantRank = participant.getHighestAchievedSeasonTier();
            // if their rank isn't null, they find its index in the list of ranks and at it to totalRank, then increment rankCount
            if (participantRank != null) {
                totalRank += ranks.indexOf(participantRank);
                rankCount++;
            }
        }
        // all ranks were null, therefore no possible way to determine avg rank of game, so reject the match
        if (rankCount == 0) return false;

        // obtain average rank index
        int avgRank = Math.round(((float) totalRank)/ rankCount);

        // if the average rank index is the index of the rank to filter by, return true (i.e accept), otherwise return false (i.e reject)
        return (avgRank == rankIndex);
    }
}
