/*
    Takes a set of input Ranks, and returns some kind of combination of them (e.g an average out of them all)

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
 */

package com.omarathon.riotapicrawler.presets.util.estimators.lib;

import com.omarathon.riotapicrawler.presets.util.Rank;

import java.util.Set;

public interface RankCombiner {
    // takes as input a set of Ranks and returns a single Rank
    // throws an IllegalArgumentException based upon the input ranks.
    Rank combine(Set<Rank> ranks) throws IllegalArgumentException;
}
