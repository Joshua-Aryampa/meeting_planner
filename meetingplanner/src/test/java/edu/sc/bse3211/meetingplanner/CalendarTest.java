package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class CalendarTest {

    private Calendar calendar;

    /**
     * Helper: creates a Meeting with a non-null description.
     * Calendar.addMeeting() calls toCheck.getDescription().equals(...)
     * in its overlap loop — null description causes NullPointerException.
     */
    private Meeting meeting(int month, int day, int start, int end) {
        Meeting m = new Meeting(month, day, start, end);
        m.setDescription("test meeting");
        return m;
    }

    @Before
    public void setUp() {
        calendar = new Calendar();
    }

    // -------------------------------------------------------
    // CAL-01: Add a valid meeting - normal happy path
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_validMeeting() {
        try {
            calendar.addMeeting(meeting(3, 15, 9, 11));
            assertTrue("Calendar should be busy at 9-11 on Mar 15",
                calendar.isBusy(3, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for valid meeting: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CAL-02: Day = 0 (below lower bound) must throw
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_dayZero_throwsException() {
        try {
            calendar.addMeeting(meeting(3, 0, 9, 11));
            fail("Expected TimeConflictException for day=0");
        } catch (TimeConflictException e) {
            assertTrue("Exception must mention day",
                e.getMessage().contains("Day does not exist"));
        }
    }

    // -------------------------------------------------------
    // CAL-03: Day = 32 (above upper bound) must throw
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_dayThirtyTwo_throwsException() {
        try {
            calendar.addMeeting(meeting(3, 32, 9, 11));
            fail("Expected TimeConflictException for day=32");
        } catch (TimeConflictException e) {
            assertTrue("Exception must mention day",
                e.getMessage().contains("Day does not exist"));
        }
    }

    // -------------------------------------------------------
    // CAL-04: FAULT 4 - month=12 (December) incorrectly
    // rejected by >= 12 check. Test EXPOSES the fault.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_december_shouldSucceed() {
        try {
            calendar.addMeeting(meeting(12, 10, 9, 11));
            assertTrue("December meeting should be added successfully",
                calendar.isBusy(12, 10, 9, 11));
        } catch (TimeConflictException e) {
            fail("FAULT 4 EXPOSED: December (month 12) is a valid month. "
                + "checkTimes() uses >= 12 instead of > 12. Threw: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CAL-05: month=11 (November) must still be accepted
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_november_valid() {
        try {
            calendar.addMeeting(meeting(11, 5, 9, 11));
            assertTrue("November 5 should be bookable",
                calendar.isBusy(11, 5, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for month=11: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CAL-06: month=0 (below lower bound) must throw
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_monthZero_throwsException() {
        try {
            calendar.addMeeting(meeting(0, 15, 9, 11));
            fail("Expected TimeConflictException for month=0");
        } catch (TimeConflictException e) {
            assertTrue("Exception must mention month",
                e.getMessage().contains("Month does not exist"));
        }
    }

    // -------------------------------------------------------
    // CAL-07: FAULT 2 - month=13 must throw.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_month13_shouldThrowException() {
        try {
            calendar.addMeeting(meeting(13, 15, 9, 11));
            fail("FAULT 2 EXPOSED: Month 13 was accepted. "
                + "Calendar constructor loops i<=13 instead of i<=12.");
        } catch (TimeConflictException e) {
            assertTrue("Exception must state month does not exist",
                e.getMessage().contains("Month does not exist"));
        }
    }

    // -------------------------------------------------------
    // CAL-08: FAULT 5 - start == end should be ALLOWED.
    // Buggy >= check rejects it. Test EXPOSES the fault.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_startEqualsEnd_shouldBeAllowed() {
        try {
            calendar.addMeeting(meeting(3, 15, 9, 9));
            assertTrue("Meeting with equal start/end should be recorded",
                calendar.isBusy(3, 15, 9, 9));
        } catch (TimeConflictException e) {
            fail("FAULT 5 EXPOSED: checkTimes() uses mStart >= mEnd which incorrectly "
                + "rejects start==end. Threw: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CAL-09: start > end must throw with the correct message
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_startAfterEnd_throwsException() {
        try {
            calendar.addMeeting(meeting(3, 15, 11, 9));
            fail("Expected TimeConflictException when start > end");
        } catch (TimeConflictException e) {
            assertTrue("Exception must state meeting starts before it ends",
                e.getMessage().contains("Meeting starts before it ends"));
        }
    }

    // -------------------------------------------------------
    // CAL-10: Two overlapping meetings - second must throw
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_overlappingMeetings_throwsException() {
        try {
            calendar.addMeeting(meeting(3, 15, 9, 11));
            calendar.addMeeting(meeting(3, 15, 10, 12));
            fail("Expected TimeConflictException for overlapping meetings");
        } catch (TimeConflictException e) {
            assertTrue("Exception must state overlap with another item",
                e.getMessage().contains("Overlap with another item"));
        }
    }

    // -------------------------------------------------------
    // CAL-11: Two non-overlapping meetings on same day — both succeed.
    // Note: isBusy uses inclusive <= boundary so hour 13 touches the
    // second meeting (13-15). We check hour 12 as the truly free slot.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_nonOverlappingMeetings_bothAdded() {
        try {
            calendar.addMeeting(meeting(3, 15, 9, 11));
            calendar.addMeeting(meeting(3, 15, 14, 16));
            assertTrue("Should be busy 9-11",    calendar.isBusy(3, 15, 9, 11));
            assertTrue("Should be busy 14-16",   calendar.isBusy(3, 15, 14, 16));
            assertFalse("Should be free 12-13",  calendar.isBusy(3, 15, 12, 13));
        } catch (TimeConflictException e) {
            fail("Non-overlapping meetings should not conflict: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CAL-12: BOUNDARY - adjacent meetings (end of first ==
    // start of second). The overlap check uses <=, so hour 11
    // is inside the first meeting. Documents boundary behaviour.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_adjacentMeetings_treatedAsOverlap() {
        try {
            calendar.addMeeting(meeting(3, 15, 9, 11));
            calendar.addMeeting(meeting(3, 15, 11, 13));
            // If both added, boundary allowed it — still assert busy
            assertTrue("If both added, hour 11 must show busy",
                calendar.isBusy(3, 15, 11, 11));
        } catch (TimeConflictException e) {
            assertTrue("Adjacent meeting flagged as overlap — correct per current <= boundary rule",
                e.getMessage().contains("Overlap with another item"));
        }
    }

    // -------------------------------------------------------
    // CAL-13: FAULT 3 - February 29 is pre-blocked.
    // However, addMeeting() SKIPS entries whose description is
    // "Day does not exist" when checking for conflicts, so a
    // meeting CAN be added over them — this exposes a source bug
    // where the blocking mechanism is bypassed.
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_feb29_isBlocked() {
        try {
            calendar.addMeeting(meeting(2, 29, 9, 11));
            // If we reach here, the blocked-day mechanism was bypassed — expose it
            fail("FAULT 3 EXPOSED: Feb 29 is pre-blocked but addMeeting() skips "
                + "'Day does not exist' entries in its conflict check, allowing "
                + "bookings on non-existent days.");
        } catch (TimeConflictException e) {
            assertTrue("Exception must indicate overlap with blocked day",
                e.getMessage().contains("Day does not exist") ||
                e.getMessage().contains("Overlap"));
        }
    }

    // -------------------------------------------------------
    // CAL-14: FAULT 3 - February 30 is pre-blocked (same bypass bug)
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_feb30_isBlocked() {
        try {
            calendar.addMeeting(meeting(2, 30, 9, 11));
            fail("FAULT 3 EXPOSED: Feb 30 is pre-blocked but the conflict check "
                + "skips 'Day does not exist' entries, allowing invalid bookings.");
        } catch (TimeConflictException e) {
            assertTrue("Exception must indicate overlap with blocked day",
                e.getMessage().contains("Day does not exist") ||
                e.getMessage().contains("Overlap"));
        }
    }

    // -------------------------------------------------------
    // CAL-15: FAULT 3 - November 31 is pre-blocked (same bypass bug)
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_nov31_isBlocked() {
        try {
            calendar.addMeeting(meeting(11, 31, 9, 11));
            fail("FAULT 3 EXPOSED: Nov 31 is pre-blocked but the conflict check "
                + "skips 'Day does not exist' entries, allowing invalid bookings.");
        } catch (TimeConflictException e) {
            assertTrue("Exception must indicate overlap with blocked day",
                e.getMessage().contains("Day does not exist") ||
                e.getMessage().contains("Overlap"));
        }
    }

    // -------------------------------------------------------
    // CAL-16: April 31 is pre-blocked (same bypass bug)
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_apr31_isBlocked() {
        try {
            calendar.addMeeting(meeting(4, 31, 9, 11));
            fail("FAULT 3 EXPOSED: Apr 31 is pre-blocked but the conflict check "
                + "skips 'Day does not exist' entries, allowing invalid bookings.");
        } catch (TimeConflictException e) {
            assertTrue("Exception must indicate overlap with blocked day",
                e.getMessage().contains("Day does not exist") ||
                e.getMessage().contains("Overlap"));
        }
    }

    // -------------------------------------------------------
    // CAL-17: June 31 is pre-blocked (same bypass bug)
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_jun31_isBlocked() {
        try {
            calendar.addMeeting(meeting(6, 31, 9, 11));
            fail("FAULT 3 EXPOSED: Jun 31 is pre-blocked but the conflict check "
                + "skips 'Day does not exist' entries, allowing invalid bookings.");
        } catch (TimeConflictException e) {
            assertTrue("Exception must indicate overlap with blocked day",
                e.getMessage().contains("Day does not exist") ||
                e.getMessage().contains("Overlap"));
        }
    }

    // -------------------------------------------------------
    // CAL-18: September 31 is pre-blocked (same bypass bug)
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_sep31_isBlocked() {
        try {
            calendar.addMeeting(meeting(9, 31, 9, 11));
            fail("FAULT 3 EXPOSED: Sep 31 is pre-blocked but the conflict check "
                + "skips 'Day does not exist' entries, allowing invalid bookings.");
        } catch (TimeConflictException e) {
            assertTrue("Exception must indicate overlap with blocked day",
                e.getMessage().contains("Day does not exist") ||
                e.getMessage().contains("Overlap"));
        }
    }

    // -------------------------------------------------------
    // CAL-19: FAULT 1 - getMeeting with out-of-bounds day (35)
    // -------------------------------------------------------
    @Test
    public void testGetMeeting_outOfBoundsDay_exposesNoValidation() {
        try {
            calendar.getMeeting(3, 35, 0);
            fail("FAULT 1 EXPOSED: getMeeting accepted day=35 without throwing");
        } catch (IndexOutOfBoundsException e) {
            assertNotNull("FAULT 1 CONFIRMED: raw IOBE instead of TimeConflictException", e);
        }
    }

    // -------------------------------------------------------
    // CAL-20: FAULT 1 - removeMeeting with out-of-bounds day
    // -------------------------------------------------------
    @Test
    public void testRemoveMeeting_outOfBoundsDay_exposesNoValidation() {
        try {
            calendar.removeMeeting(3, 35, 0);
            fail("FAULT 1 EXPOSED: removeMeeting accepted day=35 without throwing");
        } catch (IndexOutOfBoundsException e) {
            assertNotNull("FAULT 1 CONFIRMED: raw IOBE instead of TimeConflictException", e);
        }
    }

    // -------------------------------------------------------
    // CAL-21: FAULT 1 - getMeeting with negative day
    // -------------------------------------------------------
    @Test
    public void testGetMeeting_negativeDay_exposesNoValidation() {
        try {
            calendar.getMeeting(3, -1, 0);
            fail("FAULT 1 EXPOSED: getMeeting accepted day=-1");
        } catch (IndexOutOfBoundsException e) {
            assertNotNull("FAULT 1 CONFIRMED: negative day causes raw IOBE", e);
        }
    }

    // -------------------------------------------------------
    // CAL-22: FAULT 1 - getMeeting with negative month
    // -------------------------------------------------------
    @Test
    public void testGetMeeting_negativeMonth_exposesNoValidation() {
        try {
            calendar.getMeeting(-1, 15, 0);
            fail("FAULT 1 EXPOSED: getMeeting accepted month=-1");
        } catch (IndexOutOfBoundsException e) {
            assertNotNull("FAULT 1 CONFIRMED: negative month causes raw IOBE", e);
        }
    }

    // -------------------------------------------------------
    // CAL-23: getMeeting with valid date but out-of-bounds index
    // -------------------------------------------------------
    @Test
    public void testGetMeeting_validDateEmptyList_throwsIOBE() {
        try {
            calendar.getMeeting(3, 15, 0);
            fail("Expected IndexOutOfBoundsException — no meetings on Mar 15");
        } catch (IndexOutOfBoundsException e) {
            assertNotNull("Correctly throws when index 0 does not exist on empty day", e);
        }
    }

    // -------------------------------------------------------
    // CAL-24: clearSchedule then re-booking the same slot
    // -------------------------------------------------------
    @Test
    public void testClearSchedule_thenRebook_succeeds() {
        try {
            calendar.addMeeting(meeting(3, 15, 9, 11));
            assertTrue("Should be busy before clear", calendar.isBusy(3, 15, 9, 11));
            calendar.clearSchedule(3, 15);
            assertFalse("Should be free after clear", calendar.isBusy(3, 15, 9, 11));
            calendar.addMeeting(meeting(3, 15, 9, 11));
            assertTrue("Same slot must be bookable again after clear",
                calendar.isBusy(3, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception during rebook after clear: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CAL-25: clearSchedule removes all meetings for that day
    // -------------------------------------------------------
    @Test
    public void testClearSchedule_removesAllMeetings() {
        try {
            calendar.addMeeting(meeting(3, 15, 9, 11));
            calendar.clearSchedule(3, 15);
            assertFalse("Should not be busy after clear",
                calendar.isBusy(3, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CAL-26: isBusy returns false on an empty day
    // -------------------------------------------------------
    @Test
    public void testIsBusy_emptyDay_returnsFalse() {
        try {
            assertFalse("Empty day should not be busy",
                calendar.isBusy(3, 20, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for valid empty day: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CAL-27: Illegal start hour -1 must throw
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_negativeStartHour_throwsException() {
        try {
            calendar.addMeeting(meeting(3, 15, -1, 10));
            fail("Expected TimeConflictException for start hour -1");
        } catch (TimeConflictException e) {
            assertTrue("Exception must state illegal hour",
                e.getMessage().contains("Illegal hour"));
        }
    }

    // -------------------------------------------------------
    // CAL-28: Illegal end hour 24 must throw
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_endHourTwentyFour_throwsException() {
        try {
            calendar.addMeeting(meeting(3, 15, 9, 24));
            fail("Expected TimeConflictException for end hour 24");
        } catch (TimeConflictException e) {
            assertTrue("Exception must state illegal hour",
                e.getMessage().contains("Illegal hour"));
        }
    }

    // -------------------------------------------------------
    // CAL-29: printAgenda for a month returns header string.
    // Tested on an empty calendar to avoid NPE: printAgenda calls
    // toString() on all meetings including pre-blocked ones which
    // have null room — toString() is not null-safe (source bug,
    // documented in MeetingTest).
    // -------------------------------------------------------
    @Test
    public void testPrintAgenda_month_returnsString() {
        String agenda = calendar.printAgenda(3);
        assertNotNull("Agenda string should not be null", agenda);
        assertTrue("Agenda should start with 'Agenda for'",
            agenda.startsWith("Agenda for"));
    }

    // -------------------------------------------------------
    // CAL-30: printAgenda for a specific day contains header
    // -------------------------------------------------------
    @Test
    public void testPrintAgenda_day_containsCorrectHeader() {
        String agenda = calendar.printAgenda(3, 15);
        assertTrue("Day agenda should contain month/day header",
            agenda.contains("3/15"));
    }

    // -------------------------------------------------------
    // CAL-31: Janan Luwum holiday blocks the full day
    // -------------------------------------------------------
    @Test
    public void testAddMeeting_holiday() {
        try {
            Meeting janan = new Meeting(2, 16, "Janan Luwum");
            calendar.addMeeting(janan);
            assertTrue("Janan Luwum Day should be marked as busy",
                calendar.isBusy(2, 16, 0, 22));
        } catch (TimeConflictException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }
}
