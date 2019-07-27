/*
    A MatchFormatter that formats a Match into its list of TeamStats objects.
    Such list contains two entries, of which are TeamStats object that store statistics for each team.
*/

package com.omarathon.riotapicrawler.presets.matchformatters;

import com.omarathon.riotapicrawler.src.lib.MatchFormatter;
import net.rithms.riot.api.endpoints.match.dto.Match;

public class TeamStatsMatchFormatter implements MatchFormatter {
    public Object format(Match m) {
        // Obtain the list of team stats via the riot-api-java method (see documentation) and return the result
        return m.getTeams();
    }
}
