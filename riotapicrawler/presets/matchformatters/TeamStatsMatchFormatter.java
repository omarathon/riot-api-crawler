/*
    A MatchFormatter that formats a Match into its list of TeamStats objects.
    Such list contains two entries, of which are TeamStats object that store statistics for each team.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.matchformatters;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.presets.matchformatters.lib.Teams;
import com.omarathon.riotapicrawler.src.lib.formatter.MatchFormatter;

public class TeamStatsMatchFormatter extends MatchFormatter<Teams> {
    public Teams format(Match m) {
        return new Teams(m.getRedTeam(), m.getBlueTeam());
    }
}
