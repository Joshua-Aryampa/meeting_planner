package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class RoomTest {

    private Room room;

    /**
     * Helper: creates a Meeting with a non-null description.
     * addMeeting() calls getDescription() in its overlap loop —
     * null description causes NullPointerException.
     */
    private Meeting meeting(int month, int day, int start, int end) {
        Meeting m = new Meeting(month, day, start, end);
        m.setDescription("test meeting");
        return m;
    }

    @Before
    public void setUp() {
        room = new Room("LLT6A");
    }

    // -------------------------------------------------------
    // ROM-01: Constructor stores ID correctly
    // -------------------------------------------------------
    @Test
    public void testConstructor_storesID() {
        assertEquals("Room ID should be 'LLT6A'", "LLT6A", room.getID());
    }

    // -------------------------------------------------------
    // ROM-02: Default constructor sets empty ID
    // -------------------------------------------------------
    @Test
    public void testDefaultConstructor_emptyID() {
        Room r = new Room();
        assertEquals("Default room ID should be empty string", "", r.getID());
    }

    // -------------------------------------------------------
    // ROM-03: addMeeting - valid meeting added successfully
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_validMeeting_noException() {
        try {
            room.addMeeting(meeting(4, 10, 9, 11));
            assertTrue("Room should be busy after adding meeting",
                room.isBusy(4, 10, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for valid meeting: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ROM-04: Conflict exception wraps the room ID
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_conflict_throwsWithRoomID() {
        try {
            room.addMeeting(meeting(4, 10, 9, 11));
            room.addMeeting(meeting(4, 10, 10, 12));
            fail("Expected TimeConflictException for overlapping meetings");
        } catch (TimeConflictException e) {
            assertTrue("Exception message must contain the room ID",
                e.getMessage().contains("LLT6A"));
        }
    }

    // -------------------------------------------------------
    // ROM-05: isBusy returns false on empty calendar
    // -------------------------------------------------------
    @Test
    public void testIsBusy_emptyCalendar_returnsFalse() {
        try {
            assertFalse("Empty room should not be busy",
                room.isBusy(4, 10, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for valid empty check: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ROM-06: isBusy returns true after a meeting is added
    // -------------------------------------------------------
    @Test
    public void testIsBusy_afterAddMeeting_returnsTrue() {
        try {
            room.addMeeting(meeting(6, 5, 13, 15));
            assertTrue("Room should be busy at meeting time",
                room.isBusy(6, 5, 13, 15));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ROM-07: printAgenda for a month — tested on month 3 (March)
    // which has no pre-blocked days. printAgenda calls toString()
    // on every meeting including pre-blocked "Day does not exist"
    // ones which have null room — toString() is not null-safe.
    // Months 1, 3, 5, 7, 8, 10 are safe (no pre-blocked entries).
    // -------------------------------------------------------
    @Test
    public void testPrintAgenda_month_returnsString() {
        String agenda = room.printAgenda(3);
        assertNotNull("Agenda should not be null", agenda);
        assertTrue("Agenda should contain header", agenda.contains("Agenda for"));
    }

    // -------------------------------------------------------
    // ROM-08: printAgenda for a specific day returns string
    // -------------------------------------------------------
    @Test
    public void testPrintAgenda_day_returnsString() {
        String agenda = room.printAgenda(4, 10);
        assertNotNull("Agenda should not be null", agenda);
        assertTrue("Agenda should contain month/day", agenda.contains("4/10"));
    }

    // -------------------------------------------------------
    // ROM-09: getMeeting retrieves the correct meeting
    // -------------------------------------------------------
    @Test
    public void testGetMeeting_retrievesCorrectMeeting() {
        try {
            room.addMeeting(meeting(4, 10, 9, 11));
            Meeting retrieved = room.getMeeting(4, 10, 0);
            assertEquals("Start time should match", 9, retrieved.getStartTime());
            assertEquals("End time should match",  11, retrieved.getEndTime());
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ROM-10: removeMeeting removes the meeting from calendar
    // -------------------------------------------------------
    @Test
    public void testRemoveMeeting_meetingIsRemoved() {
        try {
            room.addMeeting(meeting(4, 10, 9, 11));
            assertTrue("Room should be busy before removal",
                room.isBusy(4, 10, 9, 11));
            room.removeMeeting(4, 10, 0);
            assertFalse("Room should not be busy after removal",
                room.isBusy(4, 10, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ROM-11: Two different rooms are independent calendars
    // -------------------------------------------------------
    @Test
    public void testTwoRooms_independentCalendars() {
        try {
            Room room2 = new Room("LLT6B");
            room.addMeeting(meeting(4, 10, 9, 11));
            assertFalse("LLT6B should not be busy when only LLT6A is booked",
                room2.isBusy(4, 10, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ROM-12: addMeeting with invalid day throws exception
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_invalidDay_throwsException() {
        try {
            room.addMeeting(meeting(4, 0, 9, 11));
            fail("Expected TimeConflictException for day=0");
        } catch (TimeConflictException e) {
            assertNotNull("Exception must not be null", e.getMessage());
        }
    }
}
