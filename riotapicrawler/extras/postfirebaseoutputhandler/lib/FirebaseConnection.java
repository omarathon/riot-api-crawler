/*
    Small wrapper for the firebase4j library, simply for establishing a connection.
    An Object storing a connection to a Google Firebase via the firebase4j library.
    Utilised within FirebasePostJson as the connector to post from.
    Reference: https://github.com/bane73/firebase4j

    **WARNING**: Authentication via OAuth 2.0 currently malfunctional until fixed issue within firebase4j!!

    Author: Omar Tanner, 2019 -- open source.
*/

package com.omarathon.riotapicrawler.extras.postfirebaseoutputhandler.lib;

import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.service.Firebase;
import java.io.File;
import java.io.IOException;

public class FirebaseConnection {
    private Firebase connection; // Main Object storing firebase4j connection to Firebase
    private boolean established = false;
    private boolean usingToken = false;
    private String baseUrl = null;
    private File token = null;

    /* Two constructors: 
       one with OAuth 2.0 token, one without. Both require the base url of the database. */

    // Establish connection with no OAuth 2.0 token, only with base url
    public FirebaseConnection (String baseUrl) throws FirebaseException {
        this.connection = new Firebase(baseUrl);
        this.baseUrl = baseUrl;
        established = true;
    }

    // **WARNING**: Currently manfunctional, due to issue within firebase4j (using "auth" instead of "access_token" as parameter for OAuth 2.0 API key)!!
    // Establish connection with base url and OAuth 2.0 token via File object storing its location
    public FirebaseConnection(String baseUrl, File tokenFile) throws FirebaseException, IOException {
        // Authenticate to Firebase via Auth wrapper, and generate access token.
        Auth auth = new Auth(baseUrl, tokenFile);
        String privateKey = auth.getAccessToken();
        // Finally construct object
        this.connection = new Firebase(baseUrl, privateKey);
        this.baseUrl = baseUrl;
        this.token = tokenFile;
        established = true;
        usingToken = true;
    }

    // Getter for the connection
    public Firebase get() throws IllegalStateException {
        if (!established) throw new IllegalStateException("Connection not established!");
        return this.connection;
    }

    // Getter for the base URL string in which the connection was established with
    public String getBaseUrl() {
        if (!established) throw new IllegalStateException("Connection not established!");
        return this.baseUrl;
    }

    // Getter for the OAuth 2.0 token File Object in which the connection may have been established with
    public File getToken () {
        if (!established) throw new IllegalStateException("Connection not established!");
        if (!usingToken) throw new IllegalStateException("Connection was established without an OAuth 2.0 token!");
        return this.token;
    }

    // Getter for established boolean
    public boolean isEstablished() {
        return this.established;
    }
}