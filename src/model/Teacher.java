package model;

import data.DataStore;

import java.util.ArrayList;

/**
 * The Teacher logs in with his email, chooses one of his courses,
 * chooses the session type (Lecture / Lab / Section), then types the
 * attendance ID of every student who attended and finally ends the session.
 * employeeId is a random unique number of 5 digits.
 */
public class Teacher extends Person implements Authenticatable {

    private int employeeId;
    private String password;

    public Teacher(int employeeId, String name, String email, String password) {
        super(name, email);
        this.employeeId = employeeId;
        this.password = password;
    }

    @Override
    public int getId() {
        return employeeId;
    }

    @Override
    public String getRole() {
        return "Teacher";
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean login(String email, String password) {
        return getEmail().equalsIgnoreCase(email) && this.password.equals(password);
    }

    /** The courses that the admin assigned to this teacher. */
    public ArrayList<Course> getCourses() {
        return DataStore.getCoursesOf(this);
    }

    /**
     * Step 1: start a new session for one of the teacher's courses.
     * type is "Lecture", "Lab" or "Section"; details is the topic,
     * the experiment title or the group number.
     */
    public AttendanceSession startSession(Course course, String type, String details)
            throws InvalidDataException {
        if (course.getTeacher() != this) {
            throw new InvalidDataException("You are not the teacher of this course.");
        }
        AttendanceSession session;
        if (type.equals("Lecture")) {
            session = new LectureSession(course, details);
        } else if (type.equals("Lab")) {
            session = new LabSession(course, details);
        } else {
            int group;
            try {
                group = Integer.parseInt(details.trim());
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Group number must be a number.");
            }
            session = new SectionSession(course, group);
        }
        course.addSession(session);
        return session;
    }

    /** Step 2: the student says his attendance ID and the teacher types it. */
    public Student markAttendance(AttendanceSession session, int attendanceId)
            throws InvalidDataException {
        Student s = session.getCourse().findStudent(attendanceId);
        if (s == null) {
            if (DataStore.findStudent(attendanceId) == null) {
                throw new InvalidDataException("No student has the ID " + attendanceId + ".");
            }
            throw new InvalidDataException("Student " + attendanceId + " is not enrolled in this course.");
        }
        session.markPresent(s);
        return s;
    }

    /** Step 3: end the session; every student not marked becomes absent. */
    public void endSession(AttendanceSession session) {
        session.close();
        DataStore.save();
    }
}
