package org.example;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try {
            GoogleCalendarClientFactory factory = new GoogleCalendarClientFactory();
            GoogleCalendarService calendarService = new GoogleCalendarService(factory);

            Task task = new Task(1L,
                    "Practice 7",
                    "Complete hometask 7 for Java Program Projects Work Automation",
                    LocalDate.of(2026, 6, 23));

            CalendarEventResult result = calendarService.createEvent(task);

            System.out.println("Event URL: " + result.eventUrl());

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Google Calendar event", e);
        }
    }
}
