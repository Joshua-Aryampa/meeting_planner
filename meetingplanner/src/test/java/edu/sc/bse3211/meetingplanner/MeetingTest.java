package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class MeetingTest {

    // -------------------------------------------------------
    // MTG-01: Default constructor creates a non-null object
    // -------------------------------------------------------
    @Test
    public void testDefaultConstructor_createsObject() {
        Meeting m = new Meeting();
        assertNotNull("Default constructor should create a non-null Meeting", m);
    }

    // -------------------------------------------------------
    // MTG-02: Default constructor fields are zero / null
    // -------------------------------------------------------
    @Test
    public void testDefaultConstructor_defaultFieldValues() {
        Meeting m = new Meeting();
        assertEquals("Default month should be 0",  0, m.getMonth());
        assertEquals("Default day should be 0",    0, m.getDay());
        assertEquals("Default start should be 0",  0, m.getStartTime());
        assertEquals("Default end should be 0",    0, m.getEndTime());
    }

    // -------------------------------------------------------
    // MTG-03: Vacation constructor (month, day) sets 0-23
    // -------------------------------------------------------
    @Test
    public void testVacationConstructor_setsAllDay() {
        Meeting m = new Meeting(3, 15);
        assertEquals("Month should be 3",       3,  m.getMonth());
        assertEquals("Day should be 15",        15, m.getDay());
        assertEquals("Vacation start should be 0",  0,  m.getStartTime());
        assertEquals("Vacation end should be 23",   23, m.getEndTime());
    }

    // -------------------------------------------------------
    // MTG-04: Description constructor stores description
    // and defaults to all-day (0-23)
    // -------------------------------------------------------
    @Test
    public void testDescriptionConstructor_storesDescription() {
        Meeting m = new Meeting(3, 15, "Team Standup");
        assertEquals("Description should match", "Team Standup", m.getDescription());
        assertEquals("Start should default to 0",  0,  m.getStartTime());
        assertEquals("End should default to 23",   23, m.getEndTime());
    }

    // -------------------------------------------------------
    // MTG-05: Time constructor stores all four fields
    // -------------------------------------------------------
    @Test
    public void testTimeConstructor_storesAllFields() {
        Meeting m = new Meeting(5, 20, 10, 14);
        assertEquals("Month should be 5",  5,  m.getMonth());
        assertEquals("Day should be 20",   20, m.getDay());
        assertEquals("Start should be 10", 10, m.getStartTime());
        assertEquals("End should be 14",   14, m.getEndTime());
    }

    // -------------------------------------------------------
    // MTG-06: Full constructor stores every field correctly
    // -------------------------------------------------------
    @Test
    public void testFullConstructor_storesAllFields() {
        ArrayList<Person> attendees = new ArrayList<Person>();
        attendees.add(new Person("Alice"));
        Room room = new Room("LLT6A");
        Meeting m = new Meeting(3, 15, 9, 11, attendees, room, "Sprint Review");

        assertEquals("Month",        3,              m.getMonth());
        assertEquals("Day",          15,             m.getDay());
        assertEquals("Start",        9,              m.getStartTime());
        assertEquals("End",          11,             m.getEndTime());
        assertEquals("Description",  "Sprint Review",m.getDescription());
        assertEquals("Room ID",      "LLT6A",        m.getRoom().getID());
        assertEquals("Attendee count", 1,            m.getAttendees().size());
    }

    // -------------------------------------------------------
    // MTG-07: addAttendee adds the person to the list
    // -------------------------------------------------------
    @Test
    public void testAddAttendee_addsPersonToList() {
        ArrayList<Person> attendees = new ArrayList<Person>();
        Meeting m = new Meeting(3, 15, 9, 11, attendees, new Room("LLT6A"), "Demo");
        Person alice = new Person("Alice");

        m.addAttendee(alice);
        assertTrue("Attendee list should contain Alice",
            m.getAttendees().contains(alice));
        assertEquals("Attendee count should be 1", 1, m.getAttendees().size());
    }

    // -------------------------------------------------------
    // MTG-08: removeAttendee removes the person from the list
    // -------------------------------------------------------
    @Test
    public void testRemoveAttendee_removesPersonFromList() {
        ArrayList<Person> attendees = new ArrayList<Person>();
        Person alice = new Person("Alice");
        attendees.add(alice);
        Meeting m = new Meeting(3, 15, 9, 11, attendees, new Room("LLT6A"), "Demo");

        m.removeAttendee(alice);
        assertFalse("Attendee list should not contain Alice after removal",
            m.getAttendees().contains(alice));
        assertEquals("Attendee count should be 0 after removal",
            0, m.getAttendees().size());
    }

    // -------------------------------------------------------
    // MTG-09: addAttendee then removeAttendee — list stays
    // consistent through both operations
    // -------------------------------------------------------
    @Test
    public void testAddThenRemoveAttendee_listConsistency() {
        ArrayList<Person> attendees = new ArrayList<Person>();
        Meeting m = new Meeting(3, 15, 9, 11, attendees, new Room("LLT6A"), "Demo");
        Person alice = new Person("Alice");
        Person bob   = new Person("Bob");

        m.addAttendee(alice);
        m.addAttendee(bob);
        assertEquals("Two attendees after two adds", 2, m.getAttendees().size());

        m.removeAttendee(alice);
        assertEquals("One attendee after one remove", 1, m.getAttendees().size());
        assertTrue("Bob should still be in the list", m.getAttendees().contains(bob));
        assertFalse("Alice should no longer be in the list",
            m.getAttendees().contains(alice));
    }

    // -------------------------------------------------------
    // MTG-10: Setters update every field correctly
    // -------------------------------------------------------
    @Test
    public void testSetters_updateFields() {
        Meeting m = new Meeting();
        m.setMonth(6);
        m.setDay(20);
        m.setStartTime(8);
        m.setEndTime(10);
        m.setDescription("Updated meeting");

        assertEquals("Month",       6,                 m.getMonth());
        assertEquals("Day",         20,                m.getDay());
        assertEquals("Start",       8,                 m.getStartTime());
        assertEquals("End",         10,                m.getEndTime());
        assertEquals("Description", "Updated meeting", m.getDescription());
    }

    // -------------------------------------------------------
    // MTG-11: toString on Meeting with null room AND null
    // attendees throws NullPointerException.
    // This exposes that toString() is not null-safe.
    // The failure is for the CORRECT reason: the method
    // dereferences room.getID() without a null guard.
    // -------------------------------------------------------
    @Test
    public void testToString_nullRoomAndAttendees_throwsNPE() {
        Meeting m = new Meeting(3, 15, 9, 11);
        // room and attendees were never set — both are null
        assertNull("Room should be null on this constructor path", m.getRoom());
        try {
            m.toString();
            fail("Expected NullPointerException because room is null and "
                + "toString() calls room.getID() without a null check");
        } catch (NullPointerException e) {
            // Correct failure: NPE on room.getID() confirms toString is not null-safe
            assertNotNull("NullPointerException message (may be null by JVM design)", e);
        }
    }

    // -------------------------------------------------------
    // MTG-12: toString on Meeting with null attendees but a
    // valid room — isolates WHICH null causes the NPE
    // -------------------------------------------------------
    @Test
    public void testToString_nullAttendeesValidRoom_throwsNPE() {
        ArrayList<Person> noAttendees = null;
        Room room = new Room("LLT6A");
        Meeting m = new Meeting(3, 15, 9, 11, noAttendees, room, "Demo");
        // attendees is null — toString iterates over it without a null check
        try {
            m.toString();
            fail("Expected NullPointerException because attendees list is null");
        } catch (NullPointerException e) {
            assertNotNull("NPE confirms attendees iteration has no null guard", e);
        }
    }

    // -------------------------------------------------------
    // MTG-13: toString on a fully initialised Meeting —
    // must complete without exception and contain key fields
    // -------------------------------------------------------
    @Test
    public void testToString_fullyInitialised_noException() {
        ArrayList<Person> attendees = new ArrayList<Person>();
        attendees.add(new Person("Alice"));
        Room room = new Room("LLT6A");
        Meeting m = new Meeting(3, 15, 9, 11, attendees, room, "Sprint Review");

        String result = m.toString();
        assertNotNull("toString should return a non-null string", result);
        assertFalse("toString should return a non-empty string", result.isEmpty());
    }
}
