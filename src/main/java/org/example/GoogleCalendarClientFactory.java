package org.example;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleCalendarClientFactory {
    private static final String APP_NAME = "Practice 7";

    public Calendar getClient() throws IOException {
        String credentialsPath = System.getenv("GOOGLE_CREDENTIALS_PATH");

        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IllegalStateException("Missing environment variable GOOGLE_CREDENTIALS_PATH");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/calendar"));

        try {
            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(APP_NAME).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new GoogleCalendarException("Could not initialize Google Calendar Client: " + e.getMessage());
        }
    }
}
