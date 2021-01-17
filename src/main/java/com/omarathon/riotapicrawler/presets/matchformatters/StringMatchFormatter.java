package com.omarathon.riotapicrawler.presets.matchformatters;

import com.google.gson.Gson;
import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.src.lib.formatter.Formatter;
import com.omarathon.riotapicrawler.src.lib.formatter.MatchFormatter;

public class StringMatchFormatter extends MatchFormatter<String> {
    private Formatter<Match, ?> objectFormatter;

    public StringMatchFormatter() {
        this(new Formatter<Match, Match>() {
            @Override
            public Match format(Match source) {
                return source;
            }
        });
    }

    public StringMatchFormatter(Formatter<Match, ?> objectFormatter) {
        this.objectFormatter = objectFormatter;
    }


    // toJson is polymorphic across all objects thus works with returned ? from objectFormatter.format
    public String format(Match m) {
        return new Gson().toJson(objectFormatter.format(m));
    }
}
