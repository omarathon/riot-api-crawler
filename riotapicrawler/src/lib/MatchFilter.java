/*
    An interface which predicates the filter of an input Match.

    If the Match is "accepted" by the filter, the filter method returns true,
    otherwise it returns false.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
 */

package com.omarathon.riotapicrawler.src.lib;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.match.dto.Match;

public interface MatchFilter {
    // Predicate filter method for an input Match, which may use the given RiotApi context to make api calls
    boolean filter(Match m, RiotApi api);
}
