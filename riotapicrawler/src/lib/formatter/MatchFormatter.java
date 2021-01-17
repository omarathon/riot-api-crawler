/*
    An interface which takes a Match object as input,
    and outputs an Object which should be a formatted version
    of the input Match.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.src.lib.formatter;

import com.merakianalytics.orianna.types.core.match.Match;

public abstract class MatchFormatter<F> implements Formatter<Match, F> {
}
