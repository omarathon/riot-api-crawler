/*
    An interface which predicates the filter of an input Summoner on an input Platform.

    If the Summoner on the Platform is "accepted" by the filter, the filter method returns true,
    otherwise it returns false.
 */

package com.omarathon.riotapicrawler.src.lib;

import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

public interface SummonerFilter {
    // Predicate filter method for an input Summoner on an input Platform, which may use the given RiotApi context to make api calls
    boolean filter(Summoner s, Platform platform, RiotApi api);
}
