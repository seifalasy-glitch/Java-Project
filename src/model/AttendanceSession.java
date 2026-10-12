package model;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Abstract class for one attendance sheet (one lecture, one lab or one section).
 * It is abstract because a session is always one of the three types;
 * each subclass gives its own type and its own extra detail
 * (Lecture 4 - Inheritance and Polymorphism, Lecture 5 - Abstract classes).
 */
public abstract class AttendanceSession {

    private static int nextId = 1;           // shared counter (Lecture 2 - static)

    private int sessionId;
    private Course course;
    private String date;                      // e.g. 2026-10-12
    private boolean closed;
    private ArrayList<AttendanceRecord> records = new ArrayList<AttendanceRecord>();

    /** A new session that starts today. */
    public AttendanceSession(Course course) {
        this.sessionId = nextId;
        nextId++;
        this.course = course;
        this.date = LocalDate.now().toString();
        this.closed = false;
    }

    /** A session loaded from the file. */
    public AttendanceSession(int sessionId, Course course, String date, boolean closed) {
        this.sessionId = sessionId;
        this.course = course;
        this.date = date;
        this.closed = closed;
        if (sessionId >= nextId) {
            nextId = sessionId + 1;
        }
    }

    /** "Lecture", "Lab" or "Section". */
    public abstract String getType();

    /** The extra information of each type (topic, experiment or group). */
    public abstract String getDetails();

    public int getSessionId() {
        return sessionId;
    }

    public Course getCourse() {
        return course;
    }

    public String getDate() {
        return date;
    }

    public boolean isClosed() {
        return closed;
    }

    public ArrayList<AttendanceRecord> getRecords() {
        return records;
    }

    /** Marks the student present. Refuses if the session is closed or he is already marked. */
    public void markPresent(Student s) throws InvalidDataException {
        if (closed) {
            throw new InvalidDataException("This session is already ended.");
        }
        if (isMarked(s)) {
            throw new InvalidDataException(s.getName() + " is already marked present.");
        }
        records.add(new AttendanceRecord(s, true));
    }

    /** Used only when loading old sessions from the file. */
    public void addRecord(AttendanceRecord r) {
        records.add(r);
    }

    public boolean isMarked(Student s) {
        for (AttendanceRecord r : records) {
            if (r.getStudent() == s) {
                return true;
            }
        }
        return false;
    }

    public boolean isPresent(Student s) {
        for (AttendanceRecord r : records) {
            if (r.getStudent() == s) {
                return r.isPresent();
            }
        }
        return false;
    }

    /** Ends the session: every enrolled student who was not marked becomes absent. */
    public void close() {
        if (closed) {
            return;
        }
        for (Student s : course.getStudents()) {
            if (!isMarked(s)) {
                records.add(new AttendanceRecord(s, false));
            }
        }
        closed = true;
    }

    public int countPresent() {
        int count = 0;
        for (AttendanceRecord r : records) {
            if (r.isPresent()) {
                count++;
            }
        }
        return count;
    }

    public int countAbsent() {
        return records.size() - countPresent();
    }

    @Override
    public String toString() {
        return "#" + sessionId + "  " + getType() + "  " + date + "  (" + getDetails() + ")";
    }
}
