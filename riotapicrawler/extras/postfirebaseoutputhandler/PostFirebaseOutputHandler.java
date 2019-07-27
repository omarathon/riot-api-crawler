/*
    An OutputHandler which shall POST the formatted output (from the MatchHandler) to a Google Firebase.
    Utilises Google Gson and firebase4j.
    (Refs: https://github.com/google/gson, https://github.com/bane73/firebase4j)

    Requires a FirebaseConnection object to be initialised, which may be found at https://github.com/omarathon/firebase-post-json in lib (which also utilises Auth.java).

    Logs the operation in a given log directory, in a folder named "postfirebaseoutputhandler-logs", in a file named "output-handler-log.log".
    So in file: logDirectory/postfirebaseoutputhandler-logs/output-handler-log.log
*/

package com.omarathon.riotapicrawler.extras.postfirebaseoutputhandler;

import com.google.gson.Gson;
import com.omarathon.riotapicrawler.extras.postfirebaseoutputhandler.lib.FirebaseConnection;
import com.omarathon.riotapicrawler.src.lib.MatchFormatter;
import com.omarathon.riotapicrawler.src.lib.OutputHandler;
import net.rithms.riot.api.endpoints.match.dto.Match;
import net.thegreshams.firebase4j.error.FirebaseException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class PostFirebaseOutputHandler implements OutputHandler {
    // The FirebaseConnection object used to connect to the Firebase
    private FirebaseConnection connection;
    // The path in the Firebase to which the JSON files shall be posted to
    private String path;
    // The formatter, used to convert the input Match to an Object that shall then be POSTed
    private MatchFormatter formatter;
    // The Logger object used for logging
    private Logger log;

    /* Construct this output handler with a FirebaseConnection object and the path in the Firebase to which the JSON files shall be posted to.
       Require a MatchFormatter to convert the input Match into an Object that will then be POSTed, and a Path object for the log directory to which logs shall be generated.
       Note: see lib/FirebaseConnection.java for reference on FirebaseConnection objects.
       THROWS: IOException if failed to make log directories, or if failed to initialise logger. */
    public PostFirebaseOutputHandler(FirebaseConnection connection, String path, MatchFormatter formatter, Path logDirectory) throws IOException {
        // Attempt to construct logging directory
        File logging = logDirectory.resolve("postfirebaseoutputhandler-logs").toFile();
        if (!logging.exists() || !logging.isDirectory()) {
            if (!logging.mkdirs()) throw new IOException("Failed to make the log directory!");
        }
        // Now construct the logger in the given directory
        // Initialise logger
        this.log = Logger.getLogger("RiotAPICrawlerPostFirebaseOutputHandlerLog");
        // Configure the logger with handler and formatter
        FileHandler fh = new FileHandler(logDirectory.resolve("postfirebaseoutputhandler-logs") + "/output-handler-log.log");
        this.log.addHandler(fh);
        // Set the properties
        this.connection = connection;
        this.path = path;
        this.formatter = formatter;

        log.info("[SETUP] Successfully initialised PostFirebaseOutputHandler!");
    }

    // Takes a Match, formats it into the output Object via the MatchFormatter, then posts to the Firebase within the FirebaseConnection utilising Google Gson and firebase4j
    public void handle(Match match) {
        log.info("[HANDLER] Handling Match with game ID: " + match.getGameId());
        // Format the match via the match formatter
        Object result = formatter.format(match);
        // Construct new Gson instance
        Gson gson = new Gson();
        // Use Gson instance to serialize Object result into a string JSON
        String jsonString = new Gson().toJson(result);

        log.info("[HANDLER] Match with game ID " + match.getGameId() + " into JSON String, attempting to POST!");
        // Use firebase4j to POST the map to the Google Firebase, where the Firebase object for firebase4j is obtained from the FirebaseConnection object
        try {
            connection.get().post(path, jsonString);
            log.info("[HANDLER] SUCCESS - Successfully POSTed formatted Match with game ID " + match.getGameId() + " to Firebase!");
        }
        catch (UnsupportedEncodingException | FirebaseException e) { // Error when posting
            log.severe("Failed to POST JSON to Firebase! Match game ID: " + match.getGameId());
            log.severe("Stack trace: " + e.getStackTrace());
            log.severe("Error string: " + e.toString());
        }
    }
}
