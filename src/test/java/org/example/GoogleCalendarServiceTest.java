package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoogleCalendarServiceTest {

    private GoogleCalendarService googleCalendarService;

    @Mock
    private GoogleCalendarClientFactory calendarClientFactory;

    @Mock
    private Calendar mockCalendar;

    private static final String TEST_CALENDAR_ID = "test-calendar@group.calendar.google.com";

    @BeforeEach
    void setUp() {
        googleCalendarService = new GoogleCalendarService(calendarClientFactory, TEST_CALENDAR_ID);
    }

    @Test
    void createEvent_validRequest_ReturnsEventLink() throws Exception {
        Task mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setName("Test Task");
        mockTask.setDueDate(java.time.LocalDate.now());

        Calendar.Events mockEvents = mock(Calendar.Events.class);
        Calendar.Events.Insert mockInsert = mock(Calendar.Events.Insert.class);

        when(calendarClientFactory.getClient()).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(mockEvents);

        when(mockEvents.insert(eq(TEST_CALENDAR_ID), any(Event.class))).thenReturn(mockInsert);
        when(mockInsert.setSendUpdates("all")).thenReturn(mockInsert);

        Event mockCreatedEvent = new Event()
                .setId("created-event-id")
                .setHtmlLink("https://calendar.google.com/event/123");
        when(mockInsert.execute()).thenReturn(mockCreatedEvent);

        googleCalendarService.createEvent(mockTask);

        verify(calendarClientFactory, times(1)).getClient();
        verify(mockInsert, times(1)).execute();
    }

    @Test
    void updateEvent_existingEvent_Success() throws Exception {
        Task mockTask = new Task();
        mockTask.setCalendarEventId("test-calendar-event-id");
        mockTask.setDueDate(java.time.LocalDate.now());

        Calendar.Events mockEvents = mock(Calendar.Events.class);
        Calendar.Events.Get mockGet = mock(Calendar.Events.Get.class);
        Event mockExistingEvent = new Event();

        when(calendarClientFactory.getClient()).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(mockEvents);

        when(mockEvents.get(TEST_CALENDAR_ID, mockTask.getCalendarEventId())).thenReturn(mockGet);
        when(mockGet.execute()).thenReturn(mockExistingEvent);

        Calendar.Events.Update mockUpdate = mock(Calendar.Events.Update.class);
        when(mockEvents.update(eq(TEST_CALENDAR_ID),
                eq(mockTask.getCalendarEventId()),
                any(Event.class)))
                .thenReturn(mockUpdate);
        when(mockUpdate.setSendUpdates("all")).thenReturn(mockUpdate);
        when(mockUpdate.execute()).thenReturn(new Event());

        googleCalendarService.updateEvent(mockTask);

        verify(calendarClientFactory, times(1)).getClient();
        verify(mockUpdate, times(1)).execute();
    }

    @Test
    void updateEvent_nullCalendarEventId_SkipsGoogleApiCall() throws Exception {
        Task mockTask = new Task();
        mockTask.setCalendarEventId(null);

        googleCalendarService.updateEvent(mockTask);

        verify(calendarClientFactory, never()).getClient();
    }

    @Test
    void deleteEvent_existingEvent_Success() throws Exception {
        Task mockTask = new Task();
        mockTask.setCalendarEventId("test-calendar-event-id");

        Calendar.Events mockEvents = mock(Calendar.Events.class);
        Calendar.Events.Delete mockDelete = mock(Calendar.Events.Delete.class);

        when(calendarClientFactory.getClient()).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(mockEvents);

        when(mockEvents.delete(TEST_CALENDAR_ID, mockTask.getCalendarEventId()))
                .thenReturn(mockDelete);
        when(mockDelete.setSendUpdates("all")).thenReturn(mockDelete);
        doNothing().when(mockDelete).execute();

        googleCalendarService.deleteEvent(mockTask);

        verify(calendarClientFactory, times(1)).getClient();
        verify(mockDelete, times(1)).execute();
    }

    @Test
    void deleteEvent_nullCalendarEventId_SkipsGoogleApiCall() throws Exception {
        Task mockTask = new Task();
        mockTask.setCalendarEventId(null);

        googleCalendarService.deleteEvent(mockTask);

        verify(calendarClientFactory, never()).getClient();
    }

    @Test
    void getEvents_existingEvents_ReturnAllEventsFromTheCalendar() throws Exception {
        Event event1 = new Event().setId("test-1");
        Event event2 = new Event().setId("test-2");
        Event event3 = new Event().setId("test-3");

        List<Event> items = List.of(event1, event2, event3);

        Events events = new Events();
        events.setItems(items);

        Calendar.Events calendarEvents = mock(Calendar.Events.class);
        Calendar.Events.List listRequest = mock(Calendar.Events.List.class);

        when(calendarClientFactory.getClient()).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(calendarEvents);
        when(calendarEvents.list(TEST_CALENDAR_ID)).thenReturn(listRequest);
        when(listRequest.execute()).thenReturn(events);

        Events result = googleCalendarService.getAllEvents();

        assertThat(result.getItems())
                .extracting(Event::getId)
                .containsExactlyInAnyOrder("test-1", "test-2", "test-3");
    }

    @Test
    void getEvents_noEvents_ReturnEmptyList() throws Exception {
        Events events = new Events();
        events.setItems(List.of());

        Calendar.Events calendarEvents = mock(Calendar.Events.class);
        Calendar.Events.List listRequest = mock(Calendar.Events.List.class);

        when(calendarClientFactory.getClient()).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(calendarEvents);
        when(calendarEvents.list(TEST_CALENDAR_ID)).thenReturn(listRequest);
        when(listRequest.execute()).thenReturn(events);

        Events result = googleCalendarService.getAllEvents();

        assertThat(result.getItems())
                .isEmpty();
    }

    @Test
    void createEvent_allCreatedEventsShouldHaveValidGoogleCalendarStructure() throws Exception {
        Task task1 = new Task();
        task1.setName("Test Task");
        task1.setDescription("Test Description");
        task1.setDueDate(LocalDate.now());

        Event createdEvent = new Event()
                .setId("google-1")
                .setHtmlLink("https://calendar.google.com/event/1");

        Calendar.Events.Insert insertRequest = mock(Calendar.Events.Insert.class);
        Calendar.Events calendarEvents = mock(Calendar.Events.class);

        when(calendarClientFactory.getClient()).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(calendarEvents);
        when(calendarEvents.insert(eq(TEST_CALENDAR_ID), any(Event.class)))
                .thenReturn(insertRequest);

        when(insertRequest.setSendUpdates("all")).thenReturn(insertRequest);
        when(insertRequest.execute()).thenReturn(createdEvent);

        googleCalendarService.createEvent(task1);
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(calendarEvents).insert(eq(TEST_CALENDAR_ID), eventCaptor.capture());

        Event sentEvent = eventCaptor.getValue();

        assertThat(List.of(sentEvent))
                .allMatch(e ->
                        e.getSummary().startsWith("🎯") &&
                                "confirmed".equals(e.getStatus()) &&
                                e.getStart() != null &&
                                e.getEnd() != null &&
                                "Europe/Kyiv".equals(e.getStart().getTimeZone())
                );
    }

    @Test
    void createTask_validRequest_PastValidCalendarEventIdIntoTask() throws Exception {
        // arrange
        Long expectedId = 1L;
        String expectedName = "Practice 7";
        String expectedDescription = "Complete hometask 7";
        LocalDate expectedDate = LocalDate.of(2026, 6, 23);
        String expectedGeneratedEventId = "google-event-id-999";

        Task task = new Task(expectedId, expectedName, expectedDescription, expectedDate, null);

        Calendar.Events mockEvents = mock(Calendar.Events.class);
        Calendar.Events.Insert mockInsert = mock(Calendar.Events.Insert.class);

        Event mockCreatedEvent = new Event()
                .setId(expectedGeneratedEventId)
                .setHtmlLink("https://calendar.google.com/event/123");

        when(calendarClientFactory.getClient()).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(mockEvents);
        when(mockEvents.insert(eq(TEST_CALENDAR_ID), any(Event.class))).thenReturn(mockInsert);
        when(mockInsert.setSendUpdates("all")).thenReturn(mockInsert);
        when(mockInsert.execute()).thenReturn(mockCreatedEvent);

        // act
        googleCalendarService.createEvent(task);

        // assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(task.getCalendarEventId())
                .as("Calendar Event ID should be set from Google API response")
                .isNotBlank()
                .isEqualTo(expectedGeneratedEventId);

        softly.assertThat(task.getId()).isEqualTo(expectedId);
        softly.assertThat(task.getName()).isEqualTo(expectedName);
        softly.assertThat(task.getDescription()).isEqualTo(expectedDescription);
        softly.assertThat(task.getDueDate()).isEqualTo(expectedDate);

        softly.assertAll();
    }
}
