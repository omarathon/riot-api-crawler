package com.omarathon.riotapicrawler.presets.outputhandlers.lib;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.presets.matchformatters.StringMatchFormatter;
import com.omarathon.riotapicrawler.src.lib.formatter.MatchFormatter;

public class FirebaseDataMatchFormatter extends MatchFormatter<FirebaseData> {
    private StringMatchFormatter stringMatchFormatter;
    private FirebaseDataGenerator generator;

    public FirebaseDataMatchFormatter(FirebaseDataGenerator generator) {
        this(new StringMatchFormatter(), generator);
    }

    public FirebaseDataMatchFormatter(StringMatchFormatter stringMatchFormatter, FirebaseDataGenerator generator) {
        this.stringMatchFormatter = stringMatchFormatter;
        this.generator = generator;
    }

    public FirebaseData format(Match match) {
        return generator.generate(stringMatchFormatter.format(match));
    }
}
