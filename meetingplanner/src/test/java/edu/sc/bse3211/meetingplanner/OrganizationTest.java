package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class OrganizationTest {

    private Organization org;

    @Before
    public void setUp() {
        org = new Organization();
    }

    // -------------------------------------------------------
    // ORG-01: Organization is initialized with 5 employees
    // -------------------------------------------------------
    @Test
    public void testConstructor_hasFiveEmployees() {
        assertEquals("Organization should have 5 employees", 5, org.getEmployees().size());
    }

    // -------------------------------------------------------
    // ORG-02: Organization is initialized with 5 rooms
    // -------------------------------------------------------
    @Test
    public void testConstructor_hasFiveRooms() {
        assertEquals("Organization should have 5 rooms", 5, org.getRooms().size());
    }

    // -------------------------------------------------------
    // ORG-03: getRoom returns the correct room by ID
    // -------------------------------------------------------
    @Test
    public void testGetRoom_validID_returnsRoom() {
        try {
            Room r = org.getRoom("LLT6A");
            assertNotNull("Room should not be null", r);
            assertEquals("Room ID should match", "LLT6A", r.getID());
        } catch (Exception e) {
            fail("Should not throw for valid room ID: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ORG-04: getRoom throws exception for unknown room ID
    // -------------------------------------------------------
    @Test
    public void testGetRoom_invalidID_throwsException() {
        try {
            org.getRoom("UNKNOWN_ROOM");
            fail("Expected exception for unknown room ID");
        } catch (Exception e) {
            assertTrue("Exception should mention room does not exist",
                e.getMessage().contains("does not exist"));
        }
    }

    // -------------------------------------------------------
    // ORG-05: getEmployee returns the correct person by name
    // -------------------------------------------------------
    @Test
    public void testGetEmployee_validName_returnsPerson() {
        try {
            Person p = org.getEmployee("Namugga Martha");
            assertNotNull("Person should not be null", p);
            assertEquals("Person name should match", "Namugga Martha", p.getName());
        } catch (Exception e) {
            fail("Should not throw for valid employee name: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ORG-06: getEmployee throws exception for unknown name
    // -------------------------------------------------------
    @Test
    public void testGetEmployee_invalidName_throwsException() {
        try {
            org.getEmployee("Ghost Employee");
            fail("Expected exception for unknown employee name");
        } catch (Exception e) {
            assertTrue("Exception should mention employee does not exist",
                e.getMessage().contains("does not exist"));
        }
    }

    // -------------------------------------------------------
    // ORG-07: All expected employees are present
    // -------------------------------------------------------
    @Test
    public void testConstructor_containsExpectedEmployees() {
        try {
            assertNotNull(org.getEmployee("Namugga Martha"));
            assertNotNull(org.getEmployee("Shema Collins"));
            assertNotNull(org.getEmployee("Acan Brenda"));
            assertNotNull(org.getEmployee("Kazibwe Julius"));
            assertNotNull(org.getEmployee("Kukunda Lynn"));
        } catch (Exception e) {
            fail("All expected employees should be present: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ORG-08: All expected rooms are present
    // -------------------------------------------------------
    @Test
    public void testConstructor_containsExpectedRooms() {
        try {
            assertNotNull(org.getRoom("LLT6A"));
            assertNotNull(org.getRoom("LLT6B"));
            assertNotNull(org.getRoom("LLT3A"));
            assertNotNull(org.getRoom("LLT2C"));
            assertNotNull(org.getRoom("LAB2"));
        } catch (Exception e) {
            fail("All expected rooms should be present: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // ORG-09: getEmployees returns a non-null list
    // -------------------------------------------------------
    @Test
    public void testGetEmployees_returnsNonNullList() {
        assertNotNull("Employees list should not be null", org.getEmployees());
    }

    // -------------------------------------------------------
    // ORG-10: getRooms returns a non-null list
    // -------------------------------------------------------
    @Test
    public void testGetRooms_returnsNonNullList() {
        assertNotNull("Rooms list should not be null", org.getRooms());
    }
}
