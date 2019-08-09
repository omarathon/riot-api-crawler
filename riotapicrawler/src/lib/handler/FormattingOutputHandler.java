package com.omarathon.riotapicrawler.src.lib.handler;

import com.merakianalytics.orianna.types.core.match.Match;
import com.omarathon.riotapicrawler.src.lib.formatter.MatchFormatter;

// output handler
public abstract class FormattingOutputHandler<F> extends OutputHandler {
    private MatchFormatter<F> matchFormatter;
    private Handler<F> handler;

    public FormattingOutputHandler() {
        throw new UnsupportedOperationException("Require at least a MatchFormatter (cannot assume)!");
    }

    public FormattingOutputHandler(MatchFormatter<F> matchFormatter, Handler<F> handler) {
        this.matchFormatter = matchFormatter;
        this.handler = handler;
    }

    public void handle(Match m) {
        handler.handle(matchFormatter.format(m));
    }
}
