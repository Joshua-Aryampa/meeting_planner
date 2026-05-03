package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PersonTest {

    private Person person;

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
        person = new Person("Namugga Martha");
    }

    // -------------------------------------------------------
    // PER-01: Constructor stores name correctly
    // -------------------------------------------------------
    @Test
    public void testConstructor_storesName() {
        assertEquals("Name should be 'Namugga Martha'",
            "Namugga Martha", person.getName());
    }

    // -------------------------------------------------------
    // PER-02: Default constructor sets empty name
    // -------------------------------------------------------
    @Test
    public void testDefaultConstructor_emptyName() {
        Person p = new Person();
        assertEquals("Default name should be empty string", "", p.getName());
    }

    // -------------------------------------------------------
    // PER-03: addMeeting - valid meeting added successfully
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_validMeeting_noException() {
        try {
            person.addMeeting(meeting(3, 15, 9, 11));
            assertTrue("Person should be busy after adding meeting",
                person.isBusy(3, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for valid meeting: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // PER-04: Conflict exception wraps the person's name
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_conflict_throwsWithPersonName() {
        try {
            person.addMeeting(meeting(3, 15, 9, 11));
            person.addMeeting(meeting(3, 15, 10, 12));
            fail("Expected TimeConflictException for overlapping meetings");
        } catch (TimeConflictException e) {
            assertTrue("Exception message must contain the person's name",
                e.getMessage().contains("Namugga Martha"));
        }
    }

    // -------------------------------------------------------
    // PER-05: isBusy returns false when no meetings are booked
    // -------------------------------------------------------
    @Test
    public void testIsBusy_nothingBooked_returnsFalse() {
        try {
            assertFalse("Person with no meetings should not be busy",
                person.isBusy(3, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for valid empty check: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // PER-06: isBusy returns true after a meeting is added
    // -------------------------------------------------------
    @Test
    public void testIsBusy_afterAddMeeting_returnsTrue() {
        try {
            person.addMeeting(meeting(5, 10, 14, 16));
            assertTrue("Person should be busy at meeting time",
                person.isBusy(5, 10, 14, 16));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // PER-07: printAgenda for a month returns non-empty string
    // -------------------------------------------------------
    @Test
    public void testPrintAgenda_month_returnsString() {
        String agenda = person.printAgenda(3);
        assertNotNull("Agenda should not be null", agenda);
        assertTrue("Agenda should contain header", agenda.contains("Agenda for"));
    }

    // -------------------------------------------------------
    // PER-08: printAgenda for a specific day returns string
    // -------------------------------------------------------
    @Test
    public void testPrintAgenda_day_returnsString() {
        String agenda = person.printAgenda(3, 15);
        assertNotNull("Agenda should not be null", agenda);
        assertTrue("Day agenda should contain month/day", agenda.contains("3/15"));
    }

    // -------------------------------------------------------
    // PER-09: getMeeting retrieves the correct meeting
    // -------------------------------------------------------
    @Test
    public void testGetMeeting_retrievesCorrectMeeting() {
        try {
            person.addMeeting(meeting(3, 15, 9, 11));
            Meeting retrieved = person.getMeeting(3, 15, 0);
            assertEquals("Start should match", 9, retrieved.getStartTime());
            assertEquals("End should match",  11, retrieved.getEndTime());
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // PER-10: removeMeeting removes the meeting from the calendar
    // -------------------------------------------------------
    @Test
    public void testRemoveMeeting_meetingIsRemoved() {
        try {
            person.addMeeting(meeting(3, 15, 9, 11));
            assertTrue("Should be busy before removal",
                person.isBusy(3, 15, 9, 11));
            person.removeMeeting(3, 15, 0);
            assertFalse("Should not be busy after removal",
                person.isBusy(3, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // PER-11: addMeeting with invalid month throws exception
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_invalidMonth_throwsException() {
        try {
            person.addMeeting(meeting(0, 15, 9, 11));
            fail("Expected TimeConflictException for invalid month");
        } catch (TimeConflictException e) {
            assertNotNull("Exception message must not be null", e.getMessage());
            assertFalse("Exception message must not be empty",
                e.getMessage().isEmpty());
        }
    }

    // -------------------------------------------------------
    // PER-12: Single-day vacation blocks the full day (0-23).
    // Uses description constructor so addMeeting loop does not NPE.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_singleDayVacation_blocksDay() {
        try {
            person.addMeeting(new Meeting(7, 4, "Vacation"));
            assertTrue("Full-day vacation should mark person busy",
                person.isBusy(7, 4, 0, 22));
        } catch (TimeConflictException e) {
            fail("Unexpected exception adding vacation: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // PER-13: Multi-day vacation — each day must be blocked
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_multiDayVacation_allDaysBlocked() {
        try {
            person.addMeeting(new Meeting(3, 5, "Vacation day 1"));
            person.addMeeting(new Meeting(3, 6, "Vacation day 2"));
            person.addMeeting(new Meeting(3, 7, "Vacation day 3"));
            assertTrue("Mar 5 should be blocked", person.isBusy(3, 5, 0, 22));
            assertTrue("Mar 6 should be blocked", person.isBusy(3, 6, 0, 22));
            assertTrue("Mar 7 should be blocked", person.isBusy(3, 7, 0, 22));
            assertFalse("Mar 8 should still be free", person.isBusy(3, 8, 0, 22));
        } catch (TimeConflictException e) {
            fail("Unexpected exception during multi-day vacation: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // PER-14: Vacation conflicts with an existing meeting.
    // SOURCE BUG EXPOSED: addMeeting's overlap check only tests
    // if the NEW meeting's start or end falls WITHIN an existing
    // meeting. A vacation (0-23) that completely CONTAINS a 9-11
    // meeting is NOT detected as a conflict — the system allows
    // double-booking when the new meeting wraps around an existing one.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_vacationConflictsWithExistingMeeting_throws() {
        try {
            person.addMeeting(meeting(3, 10, 9, 11));
            person.addMeeting(new Meeting(3, 10, "Vacation"));
            // If we reach here, the containment overlap was not detected — expose it
            fail("SOURCE BUG EXPOSED: A vacation (0-23) that completely contains "
                + "an existing meeting (9-11) was not detected as a conflict. "
                + "The overlap check only tests start/end within range, not containment.");
        } catch (TimeConflictException e) {
            assertTrue("Conflict message must contain person name",
                e.getMessage().contains("Namugga Martha"));
        }
    }

    // -------------------------------------------------------
    // PER-15: Vacation on a non-existent day (Feb 29).
    // SOURCE BUG EXPOSED: addMeeting skips "Day does not exist"
    // entries in its conflict loop, so vacations CAN be added
    // to pre-blocked invalid days.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_vacationOnNonExistentDay_throws() {
        try {
            person.addMeeting(new Meeting(2, 29, "Vacation"));
            fail("SOURCE BUG EXPOSED: Feb 29 is pre-blocked but addMeeting skips "
                + "'Day does not exist' entries, allowing vacation on invalid days.");
        } catch (TimeConflictException e) {
            assertTrue("Exception must indicate conflict with blocked day",
                e.getMessage().contains("Day does not exist") ||
                e.getMessage().contains("Overlap"));
        }
    }

    // -------------------------------------------------------
    // PER-16: Two people have independent calendars
    // -------------------------------------------------------
    @Test
    public void testTwoPeople_independentCalendars() {
        try {
            Person person2 = new Person("Shema Collins");
            person.addMeeting(meeting(3, 15, 9, 11));
            assertFalse("Shema should not be busy when only Martha is booked",
                person2.isBusy(3, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
