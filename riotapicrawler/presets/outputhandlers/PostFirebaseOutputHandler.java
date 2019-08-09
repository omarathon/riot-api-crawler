/*
    An OutputHandler which shall POST the formatted output (from the MatchHandler) to a Google Firebase.
    Utilises Google Gson and firebase4j.
    (Refs: https://github.com/google/gson, https://github.com/bane73/firebase4j)

    Requires a FirebaseConnection object to be initialised, which may be found at https://github.com/omarathon/firebase-post-json in lib (which also utilises Auth.java).

    Logs the operation in a given log directory, in a folder named "postfirebaseoutputhandler-logs", in a file named "output-handler-log.log".
    So in file: logDirectory/postfirebaseoutputhandler-logs/output-handler-log.log

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.outputhandlers;

import com.omarathon.riotapicrawler.presets.outputhandlers.lib.FirebaseConnection;
import com.omarathon.riotapicrawler.presets.outputhandlers.lib.FirebaseData;
import com.omarathon.riotapicrawler.presets.outputhandlers.lib.FirebaseDataMatchFormatter;
import com.omarathon.riotapicrawler.src.lib.handler.FormattingOutputHandler;
import com.omarathon.riotapicrawler.src.lib.handler.Handler;
import net.thegreshams.firebase4j.error.FirebaseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PostFirebaseOutputHandler extends FormattingOutputHandler<FirebaseData> {
    // The FirebaseConnection object used to connect to the Firebase
    private FirebaseConnection connection;
    // The path in the Firebase to which the JSON files shall be posted to
    private String path;

    /* Construct this output handler with a FirebaseConnection object and the path in the Firebase to which the JSON files shall be posted to.
       Require a MatchFormatter to convert the input Match into an Object that will then be POSTed, and a Path object for the log directory to which logs shall be generated.
       Note: see lib/FirebaseConnection.java for reference on FirebaseConnection objects.
       THROWS: IOException if failed to make log directories, or if failed to initialise logger. */
    public PostFirebaseOutputHandler(FirebaseDataMatchFormatter formatter) throws IOException {
        super(formatter, new Handler<FirebaseData>() {
            @Override
            public void handle(FirebaseData input) {
                FirebaseConnection connection = input.getConnection();
                String path = input.getPath();
                String jsonString = input.getJsonString();

                try {
                    connection.get().post(path, jsonString);
                }
                catch (FirebaseException | UnsupportedEncodingException e) {
                    // error posting
                }
            }
        });
    }
}
