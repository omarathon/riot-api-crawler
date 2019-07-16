/*
    General authentication class for Google Firebase.
    Reference: https://firebase.google.com/docs/admin/setup

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
*/

package com.omarathon.riotapicrawler.extras.postfirebaseoutputhandler.lib;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Auth {
    // Store the connection to the database as global
    private FirebaseApp app;
    // Store the File object to the auth token as global
    private File token = null;

    // Two constructors to cover the two cases: using and not using an OAuth 2.0 token.

    // Takes the website url of the base of the database as a string
    public Auth(String dbUrl) throws FileNotFoundException, IOException {
        // Begin building the FirebaseOptions with the default credentials (no token)
        FirebaseOptions.Builder o = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.getApplicationDefault());
        // Finalise building the FirebaseOptions attempt to store connection as FirebaseApp object globally (any errors here shall be propogated)
        this.app = FirebaseApp.initializeApp(setUrlAndBuild(dbUrl, o));
    }

    // Takes a File object to the authentication json file (OAuth 2.0 refresh token) and the website url to the base of the database as a string
    public Auth(String dbUrl, File tokenFile) throws FileNotFoundException, IOException {
        // Begin building the FirebaseOptions with the credentials set to the json OAuth 2.0 refresh token
        FirebaseOptions.Builder o = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(new FileInputStream(tokenFile)));
        // Finalise building the FirebaseOptions attempt to store connection as FirebaseApp object globally (any errors here shall be propogated)
        this.app =  FirebaseApp.initializeApp(setUrlAndBuild(dbUrl, o));
        this.token = tokenFile;
    }

    // Helper method which sets the database url property of the FirebaseOptions builder and then builds it, returning a FirebaseOptions object
    private FirebaseOptions setUrlAndBuild(String dbUrl, FirebaseOptions.Builder o) {
        return o.setDatabaseUrl(dbUrl).build();
    }

    // Getter for this connection to the Firebase app
    public FirebaseApp getApp() {
        return app;
    }

    // Getter for access token, if used.
    public String getAccessToken() throws IOException {
        if (token == null) throw new IllegalStateException("No token used to authenticate!");
        GoogleCredential googleCredential = GoogleCredential
            .fromStream(new FileInputStream(token))
            .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.database",
                    "https://www.googleapis.com/auth/userinfo.email"));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
      }
}