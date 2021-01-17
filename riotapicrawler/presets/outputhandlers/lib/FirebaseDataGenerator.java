package com.omarathon.riotapicrawler.presets.outputhandlers.lib;

public class FirebaseDataGenerator {
    private FirebaseConnection connection;
    private String path;

    public FirebaseDataGenerator(FirebaseConnection connection, String path) {
        this.connection = connection;
        this.path = path;
    }

    public FirebaseData generate(String jsonString) {
        return new FirebaseData(connection, path, jsonString);
    }
}
