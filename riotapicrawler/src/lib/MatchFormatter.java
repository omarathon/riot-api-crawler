/*
    An interface which takes a Match object as input,
    and outputs an Object which should be a formatted version
    of the input Match.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.src.lib;

import net.rithms.riot.api.endpoints.match.dto.Match;

public interface MatchFormatter {
    // Takes a Match object as input, formats it, and outputs a corresponding Object.
    Object format(Match match);
}
