/*
    An OutputHandler that writes a JSON file with name of the game ID for each Match object at a specified directory,
    with contents the output of the Match from the input MatchFormatter.
    Utilises Google Gson.
    (Ref: https://github.com/google/gson)

    Automatically checks the directory for any existing JSON files, and uses their names to determine which games have already been crawled.
    Stores in a HashSet the already crawled Matches, such that the output handler won't write a Match to a JSON file if it already has been written.

    Note this handler assumes the directory is not being externally modified during execution.

    Input the directory, then stores:
    - building JSON files in directory/fileoutputhandler-building,
    - built JSON files in directory/fileoutputhandler-results,
    - logs in directory/fileoutputhandler-logs/output-handler-log.log

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.presets.outputhandlers;

import com.google.gson.Gson;
import com.omarathon.riotapicrawler.src.lib.MatchFormatter;
import com.omarathon.riotapicrawler.src.lib.OutputHandler;
import net.rithms.riot.api.endpoints.match.dto.Match;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class FileOutputHandler implements OutputHandler {
    /* The directory to write the JSON files to,
       where in the case they shall be written in directory/fileoutputhandler-results,
       and the logs written in directory/fileoutputhandler-logs */
    private Path directory;
    // The formatter, used to convert the input Match to an Object that shall then be stored.
    private MatchFormatter formatter;
    // The HashSet of already stored Matches, which stores the game ID for each Match object
    private HashSet<Long> storedMatches;
    // The Logger object used to write logs
    private Logger log;

    /* INPUTS: MatchFormatter to convert the input Match to an Object that shall then be stored,
               Path object for the base directory to which the subdirectories for building, results and logs shall be generated.
      THROWS: IOException if failed to make building/results/log directories, or if failed to initialise logger */
    public FileOutputHandler(MatchFormatter formatter, Path directory) throws IOException {
        // Firstly construct the input file directory to the local system if it doesn't already exist
        File dir = directory.toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            // If fail to construct the directories mkdirs shall return false, in which case throw RuntimeError
            if (!dir.mkdirs()) throw new RuntimeException("Failed to make the base directory!");
        }
        // Now construct the building, logs and results subdirectories
        File building = directory.resolve("fileoutputhandler-building").toFile();
        if (!building.exists() || !building.isDirectory()) {
            if (!building.mkdirs()) throw new IOException("Failed to make the building directory!");
        }
        File results = directory.resolve("fileoutputhandler-results").toFile();
        if (!results.exists() || !results.isDirectory()) {
            if (!results.mkdirs()) throw new IOException("Failed to make the results directory!");
        }
        File logging = directory.resolve("fileoutputhandler-logs").toFile();
        if (!logging.exists() || !logging.isDirectory()) {
            if (!logging.mkdirs()) throw new IOException("Failed to make the log directory!");
        }

        // Initialise logger
        this.log = Logger.getLogger("RiotAPICrawlerFileOutputHandlerLog");
        // Configure the logger with handler and formatter
        FileHandler fh = new FileHandler(directory.resolve("fileoutputhandler-logs") + "/output-handler-log.log");
        this.log.addHandler(fh);

        // Set directory property
        this.directory = directory;

        // Load existing stored Matches within directory/fileoutputhandler-results into the storedMatches HashSet
        loadStoredMatches();

        // Set formatter property
        this.formatter = formatter;

        log.info("[SETUP] Successfully initialised FileOutputHandler!");
    }

    /* Fills storedMatches with the game IDs for the already stored Matches,
       assuming each Match is stored in a file of its game ID. */
    private void loadStoredMatches() {
        // Initialise storedMatches as empty HashSet
        this.storedMatches = new HashSet<Long>();
        // Obtain directory of stored Matches
        File resultsDir = directory.resolve("fileoutputhandler-results").toFile();
        // Obtain the stored matches in the directory
        File[] files = resultsDir.listFiles();
        // Add to the HashSet the name of the files, which corresponds to their Matches' game IDs
        // If the file is not convertible to a Long ID then ignore
        for (File f : files) {
            try {
                // Attempt to add to the HashSet a new Long corresponding to the filename
                storedMatches.add(new Long(f.getName()));
                log.info("[SETUP] Found Match file with game ID: " + f.getName() + " - adding to stored matches!");
            }
            catch (NumberFormatException e) { // File name not a parsable long
                log.warning("[SETUP] Found non Match file within results directory: " + f.getName());
            }
        }
    }

    /* Providing the abstract handle method, which if the Match is not already stored, shall parse the input
       Match to an Object based upon the handler's match formatter, which is defaulted
       to a formatter that changes nothing to the Match (so store all of its data).
       Then, the obtained Object shall be serialized into a JSON file via Google Gson. */
    public void handle(Match match) {
        // Store game ID of input Match
        long gameId = match.getGameId();
        // If already stored don't handle.
        if (storedMatches.contains(gameId)) {
            log.warning("[HANDLER] Already stored Match with game ID: " + gameId + " - aborting handle!");
            return;
        }
        log.info("[HANDLER] New Match with game ID: " + gameId + " now being handled.");

        // Format Match to output style via the MatchFormatter object
        Object output = formatter.format(match);
        // Now obtain the file to build to, which is directory/fileoutputhandler-building/gameId.json.tmp
        File buildFile = directory.resolve("fileoutputhandler-building/" + gameId + ".json.tmp").toFile();
        try {
            // Attempt to construct PrintWriter for build file
            PrintWriter writer = new PrintWriter(buildFile);
            // Construct a new Gson object to write the output to the build file via the above PrintWriter
            Gson gson = new Gson();
            gson.toJson(output, writer);
            // Writing finished, close the writer
            writer.close();
            // Now the file has successfully been written, move it from the building directory to the results directory (and change name to permanent JSON)
            buildFile.renameTo(directory.resolve("fileoutputhandler-results/" + gameId + ".json").toFile());
        }
        catch (FileNotFoundException e) { // Critical error, directory must have been interfered with haphazardly
            log.severe("[HANDLER] Failed to handle match with game ID: " + gameId + " - failed to find new file before writing!");
            log.severe("Stack trace: " + e.getStackTrace());
            log.severe("Error string: " + e.toString());
        }
    }

    // A setter for the MatchFormatter, if one wishes to use a formatter besides the default.
    public void setFormatter(MatchFormatter formatter) {
        this.formatter = formatter;
        log.severe("MatchFormatter changed!");
    }
}
