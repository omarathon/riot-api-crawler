/*
    A MatchFilter that rejects Matches shorter than 20 minutes.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchfilters;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.src.lib.filter.MatchFilter;
import org.joda.time.Duration;

public class GameDurationMatchFilter extends MatchFilter {
    public boolean filter(Match m) {
        // if duration of Match is longer than or equal to 20 mins, return true, otherwise return false.
        Duration matchDuration = m.getDuration();
        Duration mins = Duration.standardMinutes(20);
        return (matchDuration.isLongerThan(mins) || matchDuration.isEqual(mins));
    }
}
