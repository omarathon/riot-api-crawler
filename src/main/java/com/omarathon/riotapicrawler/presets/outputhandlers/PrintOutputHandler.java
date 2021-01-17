/*
    A simple OutputHandler which prints an input Match to System.out.
    Uses a MatchFormatter to format the input Match into its printable output.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
 */

package com.omarathon.riotapicrawler.presets.outputhandlers;

import com.omarathon.riotapicrawler.presets.matchformatters.StringMatchFormatter;
import com.omarathon.riotapicrawler.src.lib.handler.FormattingOutputHandler;
import com.omarathon.riotapicrawler.src.lib.handler.Handler;

public class PrintOutputHandler extends FormattingOutputHandler<String> {
    public PrintOutputHandler() {
        this(new StringMatchFormatter());
    }

    public PrintOutputHandler(StringMatchFormatter formatter) {
        super(formatter, new Handler<String>() {
            @Override
            public void handle(String input) {
                System.out.println(input);
            }
        });
    }
}
