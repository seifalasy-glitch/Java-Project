package model;

/**
 * A student does not log in. The admin creates him and enrolls him in courses.
 * studentAttendanceId is a random unique number of 4 digits; the student
 * says it to the teacher to be marked present.
 * Implements Comparable so a list of students can be sorted by name (Lecture 5).
 */
public class Student extends Person implements Comparable<Student> {

    private int studentAttendanceId;

    public Student(int studentAttendanceId, String name, String email) {
        super(name, email);
        this.studentAttendanceId = studentAttendanceId;
    }

    public int getStudentAttendanceId() {
        return studentAttendanceId;
    }

    @Override
    public int getId() {
        return studentAttendanceId;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public int compareTo(Student other) {
        return getName().compareToIgnoreCase(other.getName());
    }
}
