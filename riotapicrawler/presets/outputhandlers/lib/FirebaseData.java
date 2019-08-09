package com.omarathon.riotapicrawler.presets.outputhandlers.lib;


public class FirebaseData {
    private FirebaseConnection connection;
    private String path;
    private String jsonString;

    public FirebaseData(FirebaseConnection connection, String path, String jsonString) {
        this.connection = connection;
        this.path = path;
        this.jsonString = jsonString;
    }

    public FirebaseConnection getConnection() {
        return connection;
    }

    public String getPath() {
        return path;
    }

    public String getJsonString() {
        return jsonString;
    }
}
