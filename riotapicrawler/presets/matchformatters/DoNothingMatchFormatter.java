/*
    A MatchFormatter that simply passes through the input match without any modification.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchformatters;

import com.omarathon.riotapicrawler.src.lib.MatchFormatter;
import net.rithms.riot.api.endpoints.match.dto.Match;

public class DoNothingMatchFormatter implements MatchFormatter {
    public Object format(Match m) {
        // Pass through the input match without modification.
        return m;
    }
}
