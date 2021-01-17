/*
    An abstract class which predicates the filter of an input Match.

    Is a ContextFilter that filters Match objects, with context in the filter method the RiotApi instance.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
 */

package com.omarathon.riotapicrawler.src.lib.filter;

import com.merakianalytics.orianna.types.core.match.Match;

public abstract class MatchFilter extends GhostFilter<Match> {
}
