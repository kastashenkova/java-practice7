package org.example;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class GoogleCalendarService {
    private static final ZoneId KYIV_ZONE = ZoneId.of("Europe/Kyiv");

    private final GoogleCalendarClientFactory calendarClientFactory;
    private final String targetCalendarId;

    public GoogleCalendarService(GoogleCalendarClientFactory calendarClientFactory) {
        this(calendarClientFactory, System.getenv("CALENDAR_ID"));
    }

    public GoogleCalendarService(GoogleCalendarClientFactory calendarClientFactory, String targetCalendarId) {
        this.calendarClientFactory = calendarClientFactory;
        this.targetCalendarId = targetCalendarId;

        if (this.targetCalendarId == null || this.targetCalendarId.isEmpty()) {
            throw new IllegalStateException("Target calendar ID is null or empty: " + this.targetCalendarId);
        }
    }

    public CalendarEventResult createEvent(Task task) {
        Calendar client = null;
        try {
            client = calendarClientFactory.getClient();
        } catch (IOException e) {
            throw new GoogleCalendarException("Could not get client from Google Calendar Client: " + e.getMessage());
        }

        ZonedDateTime start = ZonedDateTime.now(KYIV_ZONE);
        ZonedDateTime end = task.getDueDate()
                .atTime(18, 30)
                .atZone(KYIV_ZONE);

        if (start.isAfter(end)) {
            end = start.plusWeeks(1);
        }

        Event event = new Event()
                .setSummary("\uD83C\uDFAF " + task.getName())
                .setDescription(buildDescription(task))
                .setStart(toEventDateTime(start))
                .setEnd(toEventDateTime(end))
                .setStatus("confirmed")
                .setGuestsCanSeeOtherGuests(true)
                .setGuestsCanModify(false)
                .setGuestsCanInviteOthers(false);

        Event created = null;
        try {
            created = client.events()
                    .insert(targetCalendarId, event)
                    .setSendUpdates("all")
                    .execute();
        } catch (IOException e) {
            throw new GoogleCalendarException("Could not create event: " + e.getMessage());
        }

        task.setCalendarEventId(created.getId());
        return new CalendarEventResult(created.getId(), created.getHtmlLink());
    }

    public void updateEvent(Task task) throws IOException {
        if (task.getCalendarEventId() == null) {
            return;
        }

        Calendar client = calendarClientFactory.getClient();

        Event event = client.events()
                .get(targetCalendarId, task.getCalendarEventId())
                .execute();

        ZonedDateTime start = ZonedDateTime.now(KYIV_ZONE);
        ZonedDateTime end = task.getDueDate()
                .atTime(18, 30)
                .atZone(KYIV_ZONE);

        event.setSummary("\uD83C\uDFAF " + task.getName());
        event.setDescription(buildDescription(task));
        event.setStart(toEventDateTime(start));
        event.setEnd(toEventDateTime(end));

        client.events()
                .update(targetCalendarId, task.getCalendarEventId(), event)
                .setSendUpdates("all")
                .execute();
    }

    public void deleteEvent(Task task) throws IOException {
        if (task.getCalendarEventId() == null) {
            return;
        }

        calendarClientFactory.getClient()
                .events()
                .delete(targetCalendarId, task.getCalendarEventId())
                .setSendUpdates("all")
                .execute();
    }

    public Events getAllEvents() throws IOException {
        Calendar client = calendarClientFactory.getClient();

        return client.events()
                .list(targetCalendarId)
                .execute();
    }

    private EventDateTime toEventDateTime(ZonedDateTime zdt) {
        return new EventDateTime()
                .setDateTime(new DateTime(zdt.toInstant().toEpochMilli()))
                .setTimeZone(KYIV_ZONE.toString());
    }

    private String buildDescription(Task task) {
        return String.format("""
                Task ID: %s
                Description: %s
                """,
                task.getId(),
                task.getDescription()
        );
    }
}
