package org.example;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.util.Collections;

public class GoogleCalendarClientFactory {
    private static final String APP_NAME = "Practice 7";

    public Calendar getClient() throws Exception {
        String credentialsPath = System.getenv("GOOGLE_CREDENTIALS_PATH");

        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IllegalStateException("Вкажіть GOOGLE_CREDENTIALS_PATH у змінних середовища (.env)");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/calendar"));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName(APP_NAME).build();
    }
}
