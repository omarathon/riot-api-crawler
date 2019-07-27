/*
*  A helper class which obtains the <Rank, Tier> pair
*  for a Summoner on a given platform in a given queue type.
* */

package com.omarathon.riotapicrawler.src.lib.helper;

import javafx.util.Pair;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.league.dto.LeaguePosition;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.Set;

public class EloGetter {
    // Requires the RiotApi instance to make the api calls
    public static Pair<String, String> getElo(RiotApi api, Summoner s, Platform p, String queueType) throws RiotApiException {
        // obtain league positions
        Set<LeaguePosition> leaguePositions = api.getLeaguePositionsBySummonerId(p, s.getId());
        // iterate through each league position to find the league position in the given queueType
        LeaguePosition position = null;
        for (LeaguePosition leaguePosition : leaguePositions) {
            if (leaguePosition.getQueueType() == queueType) {
                position = leaguePosition;
            }
        }
        // if position is still null, no position on given queue type found so return null for not found
        if (position == null) return null;

        // a position has been found, so obtain the rank and tier and return
        return new Pair<String, String>(position.getRank(), position.getTier());
    }
}
