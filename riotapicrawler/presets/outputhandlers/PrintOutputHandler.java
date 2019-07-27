/* *
    A simple OutputHandler which prints an input Match to System.out.
    Uses a MatchFormatter to format the input Match into its printable output.
 */

package com.omarathon.riotapicrawler.presets.outputhandlers;

import com.google.gson.Gson;
import com.omarathon.riotapicrawler.src.lib.MatchFormatter;
import com.omarathon.riotapicrawler.src.lib.OutputHandler;
import net.rithms.riot.api.endpoints.match.dto.Match;

public class PrintOutputHandler implements OutputHandler {
    private MatchFormatter formatter;

    // INPUT: A MatchFormatter which shall format the Match to then be printed
    public PrintOutputHandler(MatchFormatter formatter) {
        this.formatter = formatter;
    }

    // The handle method shall format the input Match and print it to System.out. Deserialises the Object into a String via Google Gson.
    public void handle(Match m) {
        System.out.println(new Gson().toJson(formatter.format(m)));
    }
}
