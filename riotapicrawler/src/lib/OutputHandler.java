/*
    An interface which takes a Match object and does something with it,
    for example formatting and saving it to a JSON file, or uploading it to a database.

    One must instantiate this handler, where in the constructor one must set up the handler
    such that when handle is called at any instant, the input Match shall be handled.

    An example of this would be instantiating the class with a property variable storing
    the directory to write the Matches to, where in the constructor such property is set.
    Then, a call to handle will use such property variable to write the Match to the given
    directory.

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
 */

package com.omarathon.riotapicrawler.src.lib;

import net.rithms.riot.api.endpoints.match.dto.Match;

public interface OutputHandler {
    // Takes a Match object as input and does something with it
    void handle(Match m);
}
