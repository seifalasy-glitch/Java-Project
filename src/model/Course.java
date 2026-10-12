package model;

import java.util.ArrayList;

/**
 * A course has one teacher, a list of enrolled students
 * and a list of attendance sessions (lectures, labs and sections).
 */
public class Course {

    private String code;          // unique, e.g. COMP301
    private String name;
    private Teacher teacher;      // can be null until the admin assigns one
    private ArrayList<Student> students = new ArrayList<Student>();
    private ArrayList<AttendanceSession> sessions = new ArrayList<AttendanceSession>();

    public Course(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<AttendanceSession> getSessions() {
        return sessions;
    }

    public void enrollStudent(Student s) throws InvalidDataException {
        if (students.contains(s)) {
            throw new InvalidDataException(s.getName() + " is already enrolled in " + code + ".");
        }
        students.add(s);
    }

    public void removeStudent(Student s) {
        students.remove(s);
    }

    public boolean isEnrolled(Student s) {
        return students.contains(s);
    }

    /** Returns the enrolled student with this attendance ID, or null. */
    public Student findStudent(int attendanceId) {
        for (Student s : students) {
            if (s.getStudentAttendanceId() == attendanceId) {
                return s;
            }
        }
        return null;
    }

    public void addSession(AttendanceSession session) {
        sessions.add(session);
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
