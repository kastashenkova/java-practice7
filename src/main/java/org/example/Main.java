package org.example;

import java.time.LocalDate;
import java.time.Month;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log =
            LoggerFactory.getLogger(Main.class);

    static void main() {
        try {
            GoogleCalendarClientFactory factory = new GoogleCalendarClientFactory();
            GoogleCalendarService calendarService = new GoogleCalendarService(factory);

            Task task = new Task(1L,
                    "Practice 7",
                    "Complete homework 7 for Java Program Projects Work Automation",
                    LocalDate.of(2026, Month.JUNE, 23));

            CalendarEventResult result = calendarService.createEvent(task);
            log.info("Event URL: {}", result.eventUrl());

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Google Calendar event", e);
        }
    }
}
