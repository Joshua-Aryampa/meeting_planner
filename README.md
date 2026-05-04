# BSE3211 — Meeting Planner Unit Tests
## README — How to Set Up and Run the Tests

---

## What This Project Is

This is a Maven Java project containing unit tests for the **Meeting Planner** calendar application.
The tests are written in **JUnit 4** and cover five classes: `Calendar`, `Meeting`, `Person`, `Room`, and `Organization`.

There are **82 tests** in total. When you run them against the original (unmodified) source code, **72 will pass and 10 will intentionally fail** — the 10 failures expose real bugs in the source code.

---

## Prerequisites

Before you can run the tests, make sure you have the following installed:

| Tool | Minimum Version | How to Check |
|------|----------------|--------------|
| Java JDK | 8 or later | `java -version` |
| Apache Maven | 3.x | `mvn -version` |

### Installing Java
Download from: https://www.java.com/en/download/

### Installing Apache Maven
1. Download the **Binary zip archive** from: https://maven.apache.org/download.cgi
2. Unzip it to a folder, e.g. `C:\Program Files\Apache\maven`
3. Add the `bin` folder to your system PATH:
   - Search **Environment Variables** in Windows
   - Edit the **Path** system variable
   - Add the full path to the `bin` folder (e.g. `C:\Program Files\Apache\maven\bin`)
4. Open a **new** terminal and verify: `mvn -version`

---

## Project Structure

```
meetingplanner/
└── meetingplanner/
    ├── pom.xml                          ← Maven config file (do not delete)
    └── src/
        ├── main/
        │   └── java/edu/sc/bse3211/meetingplanner/
        │       ├── Calendar.java
        │       ├── Meeting.java
        │       ├── Organization.java
        │       ├── Person.java
        │       ├── PlannerInterface.java
        │       ├── Room.java
        │       └── TimeConflictException.java
        └── test/
            └── java/edu/sc/bse3211/meetingplanner/
                ├── CalendarTest.java    ← 31 tests
                ├── MeetingTest.java     ← 13 tests
                ├── OrganizationTest.java← 10 tests
                ├── PersonTest.java      ← 16 tests
                └── RoomTest.java        ← 12 tests
```

---

## Step 1 — Place the Test Files

The five test files go into:
```
meetingplanner\meetingplanner\src\test\java\edu\sc\bse3211\meetingplanner\
```

Copy and **replace** the five existing stub files with the provided test files:
- `CalendarTest.java`
- `MeetingTest.java`
- `OrganizationTest.java`
- `PersonTest.java`
- `RoomTest.java`

---

## Step 2 — Fix the Java Version in pom.xml

The `pom.xml` file may be set to Java 7 which is no longer supported by modern Java. Open `pom.xml` and find:

```xml
<source>7</source>
<target>7</target>
```

Change both values to `8`:

```xml
<source>8</source>
<target>8</target>
```

Save the file. You only need to do this once.

---

## Step 3 — Run the Tests

Open a terminal (PowerShell or Command Prompt) and navigate to the **inner** `meetingplanner` folder — the one that contains `pom.xml`:

```powershell
cd "path\to\meetingplanner\meetingplanner"
```

For example:
```powershell
cd "C:\Users\YourName\Downloads\Unit Testing Exercise\meetingplanner\meetingplanner"
```

Then run:
```powershell
mvn test
```

Maven will compile the source code, compile the tests, and run all 82 tests. This may take a minute on the first run as Maven downloads dependencies.

---

## Step 4 — Understanding the Output

### What a passing test looks like
```
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
```
All tests in that class passed.

### What the overall result looks like (expected)
```
Tests run: 82, Failures: 10, Errors: 0, Skipped: 0
```

### The 10 expected failures — and what they mean

| Failing Test | Reason |
|---|---|
| `CalendarTest.testAddMeeting_december_shouldSucceed` | **Fault 4** — December rejected by `>= 12` bug |
| `CalendarTest.testAddMeeting_startEqualsEnd_shouldBeAllowed` | **Fault 5** — Equal start/end rejected by `>=` bug |
| `CalendarTest.testAddMeeting_feb29_isBlocked` | **Fault 3** — Blocked-day bypass: Feb 29 can be booked |
| `CalendarTest.testAddMeeting_feb30_isBlocked` | **Fault 3** — Blocked-day bypass: Feb 30 can be booked |
| `CalendarTest.testAddMeeting_nov31_isBlocked` | **Fault 3** — Blocked-day bypass: Nov 31 can be booked |
| `CalendarTest.testAddMeeting_apr31_isBlocked` | **Fault 3** — Blocked-day bypass: Apr 31 can be booked |
| `CalendarTest.testAddMeeting_jun31_isBlocked` | **Fault 3** — Blocked-day bypass: Jun 31 can be booked |
| `CalendarTest.testAddMeeting_sep31_isBlocked` | **Fault 3** — Blocked-day bypass: Sep 31 can be booked |
| `PersonTest.testAddMeeting_vacationConflictsWithExistingMeeting_throws` | **Bug A** — Containment overlap not detected |
| `PersonTest.testAddMeeting_vacationOnNonExistentDay_throws` | **Fault 3** — Blocked-day bypass via Person |

> **These failures are correct and expected.** Each one exposes a real bug in the source code.
> If someone fixed the bugs in the source, these tests would start passing.

---

## Running a Single Test Class

To run only one test class at a time:

```powershell
mvn test -Dtest=CalendarTest
mvn test -Dtest=MeetingTest
mvn test -Dtest=OrganizationTest
mvn test -Dtest=PersonTest
mvn test -Dtest=RoomTest
```

---

## Running a Single Test Method

To run one specific test:

```powershell
mvn test -Dtest=CalendarTest#testAddMeeting_validMeeting
```

---

## Viewing Detailed Test Reports

After running `mvn test`, detailed reports are saved here:
```
meetingplanner\meetingplanner\target\surefire-reports\
```

Each file is named after its test class (e.g. `edu.sc.bse3211.meetingplanner.CalendarTest.txt`) and contains the full output including stack traces for any failures.

---

## Troubleshooting

| Problem | Solution |
|---|---|
| `mvn not recognized` | Maven is not on your PATH — see Prerequisites section above |
| `Source option 7 is no longer supported` | Change `<source>7</source>` to `<source>8</source>` in pom.xml |
| `duplicate method` compilation error | You have an old and new version of a test file merged together — replace the entire file with the provided version |
| `NullPointerException` in tests | Make sure you are using the latest versions of all 5 test files |
| More than 10 failures | Check that you replaced all 5 test stub files with the provided versions |

---

## Quick Reference — Test Counts

| Test Class | Tests | Expected Pass | Expected Fail |
|---|---|---|---|
| CalendarTest | 31 | 23 | 8 |
| MeetingTest | 13 | 13 | 0 |
| OrganizationTest | 10 | 10 | 0 |
| PersonTest | 16 | 14 | 2 |
| RoomTest | 12 | 12 | 0 |
| **Total** | **82** | **72** | **10** |
